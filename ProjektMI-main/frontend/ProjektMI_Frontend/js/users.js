import {createFooter, createHeader} from './neutralne.js'





const userMap = new Map();

function displayUsers(users) {
    const userTab = document.getElementById('userBody');


    users.forEach(user => {
        const col = document.createElement('tr');
        const currentRole = user.rola || 'STUDENT';

        col.className = 'user-row';
        col.dataset.userId = user.idPouzivatel;
        col.innerHTML = `

          <tr>
            <td>${user.meno}</td>
            <td>${user.priezvisko}</td>
            <td>${user.email}</td>
            <td>${user.telCislo}</td>
            <td class="role-cell">
                <select class="role-select" data-current-role="${currentRole}">
                    <option value="STUDENT" ${currentRole === 'STUDENT' ? 'selected' : ''}>Študent</option>
                    <option value="UCITEL" ${currentRole === 'UCITEL' ? 'selected' : ''}>Učiteľ</option>
                    <option value="ADMIN" ${currentRole === 'ADMIN' ? 'selected' : ''}>Administrator</option>
                </select>
            </td>
            <td>${user.ulica}</td>
            <td>${user.mesto}</td>
            <td>${user.psc}</td>
          </tr>
        `;

        col.addEventListener('click', (event) => {
            // Check if the clicked element is NOT within the "Role" cell
            if (!event.target.closest('.role-cell')) {
                const userId = col.dataset.userId;
                window.location.href = `userOrders.html?id=${userId}`;
            }
        });



        const selectElement = col.querySelector('.role-select');
        selectElement.addEventListener('change', () => {
            const previousRole = selectElement.dataset.currentRole;
            const newRole = selectElement.value

            if (newRole !== previousRole) {
                userMap.set(user.idPouzivatel, newRole);
            }
        });
        userTab.appendChild(col);
    });
}

function saveAllUserRoles() {
    const changeUser = {};
    userMap.forEach((value, key) => {
        changeUser[key] = value;
    })


    console.log(changeUser)

    if (Object.keys(changeUser).length > 0) {
        axios.put('http://localhost:8080/update-rola', changeUser, {
            headers: {
                'Content-Type': 'application/json'
            },
            withCredentials:true
        }).then(response => {
            if (response.status === 200) {
                window.location.reload();
            } else {
                alert('Nepodarilo sa zmenit rolu');
            }
        });
    }
}


async function fetchUsers() {
    try {
        const response = await axios.get('http://localhost:8080/get-all-users', {withCredentials: true});
        const users = response.data;

        displayUsers(users);
    } catch (error) {
        console.error('Error fetching users:', error);
        if (error.response) {
            console.error('Server responded with:', error.response.data);
        }
    }
}

document.addEventListener("DOMContentLoaded", function () {
    document.body.insertBefore(createHeader(), document.body.firstChild);
    document.body.insertBefore(createFooter(), document.body.lastChild);
    fetchUsers();


    document.getElementById('ulozit').addEventListener('click', (event) => {
        saveAllUserRoles();
    });
});

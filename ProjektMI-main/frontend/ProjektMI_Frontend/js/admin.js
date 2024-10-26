//import {fetchUser} from "./helpers.js";

function includeHTML() {
    var z, i, elmnt, file, xhttp;
    /* Loop through a collection of all HTML elements: */
    z = document.getElementsByTagName("*");
    for (i = 0; i < z.length; i++) {
        elmnt = z[i];
        /*search for elements with a certain atrribute:*/
        file = elmnt.getAttribute("w3-include-html");
        if (file) {
            /* Make an HTTP request using the attribute value as the file name: */
            xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = function () {
                if (this.readyState == 4) {
                    if (this.status == 200) {
                        elmnt.innerHTML = this.responseText;
                    }
                    if (this.status == 404) {
                        elmnt.innerHTML = "Page not found.";
                    }
                    /* Remove the attribute, and call this function once more: */
                    elmnt.removeAttribute("w3-include-html");
                    includeHTML();
                }
            }
            xhttp.open("GET", file, true);
            xhttp.send();
            /* Exit the function: */
            return;
        }
    }
}


function displayUsers(users) {
    const userTab = document.getElementById('userTab');

    users.forEach(user => {
        const col = document.createElement('tr');
        col.className = '';
        col.innerHTML = `
<!--            // <div class="checkbox-container">-->
<!--            //     <input type="checkbox" class="product-checkbox" value="${user.id}">-->
<!--            // </div>-->
          <tr>
            <td>${user.meno}</td>
            <td>${user.priezvisko}</td>
            <td>${user.email}</td>
            <td>${user.telCislo}</td>
            <td>${user.telCislo}</td>
            <td>${user.ulica}</td>
            <td>${user.mesto}</td>
            <td>${user.psc}</td>
          </tr>
        `;
        userTab.appendChild(col);
    });


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
    fetchUsers().then(response => {
        console.log("User outside async function (from global variable):", response);
        displayUsers(response);
    });
});

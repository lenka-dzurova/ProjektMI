import {createFooter, createHeader, updateCartCount} from './neutralne.js';


let meno;


async function fetchOrders() {
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('id');
    const requestData = {
        idPouzivatel: userId,
    };
    try {
        const response = await axios.post('http://localhost:8080/objednavka/get-all-by-user-id',requestData, {withCredentials: true});
        let orders = response.data;
        displayUsers(orders);
    } catch (error) {
        console.error('Error fetching users:', error);
        if (error.response) {
            console.error('Server responded with:', error.response.data);
        }
    }
}

function displayUsers(orders) {
    const orderTab = document.getElementById('orderTable');

    orders.forEach(order => {
        const row = document.createElement('tr');

        const date = new Date(order.datumObjednavky);

        // Formátuj dátum ako deň, mesiac, rok
        const formattedDate = `${date.getDate()}. ${date.getMonth() + 1}. ${date.getFullYear()}`;
        row.dataset.orderId = order.idObjednavka;

        meno = order.pouzivatel.meno + " " + order.pouzivatel.priezvisko;



        row.className = 'order-row';
        row.innerHTML = `
          <tr>
            <td>${order.idObjednavka}</td>
            <td>${formattedDate}</td>
            <td>${order.stavObjednavky}</td>
          </tr>
        `;

        row.addEventListener('click', (event) => {
            // Check if the clicked element is NOT within the "Role" cell
            const orderId = row.dataset.orderId;
            window.location.href = `productsOrder.html?orderId=${orderId}`;
        });

        orderTab.appendChild(row);
    });
    document.getElementById('orderTitle').textContent += ": " + meno;
}

document.addEventListener("DOMContentLoaded", function () {
    document.body.insertBefore(createHeader(), document.body.firstChild);
    document.body.insertBefore(createFooter(), document.body.lastChild);
    fetchOrders();
    updateCartCount();



});
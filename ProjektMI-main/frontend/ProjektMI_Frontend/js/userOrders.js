

let meno;

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

    fetchOrders();
    includeHTML();



});
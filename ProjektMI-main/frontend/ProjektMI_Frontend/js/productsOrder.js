import {fetchUser} from "./helpers.js";
import {createFooter, createHeader} from "./neutralne.js";


let orderId;




async function fetchUsers(rola) {
    const urlParams = new URLSearchParams(window.location.search);
    orderId = urlParams.get('orderId');

    const requestData = {
        idObjednavka: orderId,
    };
    try {
        const response = await axios.post('http://localhost:8080/objednavka/get-products-by-order-id',requestData, {withCredentials: true});
        let products = response.data;
        console.log(products);
        displayUsers(products, rola);
    } catch (error) {
        console.error('Error fetching users:', error);
        if (error.response) {
            console.error('Server responded with:', error.response.data);
        }
    }
}

// function formatDate(dateString) {
//     const [day, month, year] = dateString.split('.').map(part => part.trim());
//     return `${year}-${month}-${day}`;  // Formát pre JavaScript
// }

function displayUsers(products, rola) {

    const productTab = document.getElementById('productsBody');

    products.forEach(product => {
        const row = document.createElement('tr');
        row.className = 'product-row';

        // Format the date for the input field (datumVratenia)
        // const formattedDatumObjednavky = formatDate(product.datumVratenia);
        row.innerHTML = `
            <td>${product.produkt.idProdukt}</td>
            <td>${product.produkt.nazov}</td>
          
        `;

        productTab.appendChild(row);

        console.info(rola);


    });
}

document.addEventListener("DOMContentLoaded", function () {
    document.body.insertBefore(createHeader(), document.body.firstChild);
    document.body.insertBefore(createFooter(), document.body.lastChild);



    fetchUser().then(response => {
        let rola = response.rola;
        fetchUsers(rola);

    });


    if (sessionStorage.getItem('showToastr') === 'true') {
        toastr.info("Vykonanie zmien bolo zrušené");
        sessionStorage.removeItem('showToastr'); // Vymažeme informáciu z sessionStorage
    }





});




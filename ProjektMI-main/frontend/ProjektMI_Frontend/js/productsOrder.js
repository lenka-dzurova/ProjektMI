import {fetchUser} from "./helpers.js";

let updateDatumy = [];
let orderId;
let saveButton;

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

async function fetchUsers(rola) {
    const urlParams = new URLSearchParams(window.location.search);
    orderId = urlParams.get('orderId');

    const requestData = {
        idObjednavka: orderId,
    };
    try {
        const response = await axios.post('http://localhost:8080/objednavka/get-products-by-order-id',requestData, {withCredentials: true});
        let products = response.data;
        displayUsers(products, rola);
    } catch (error) {
        console.error('Error fetching users:', error);
        if (error.response) {
            console.error('Server responded with:', error.response.data);
        }
    }
}

function formatDate(dateString) {
    const [day, month, year] = dateString.split('.').map(part => part.trim());
    return `${year}-${month}-${day}`;  // Formát pre JavaScript
}

function displayUsers(products, rola) {

    const productTab = document.getElementById('productsBody');

    products.forEach(product => {
        const row = document.createElement('tr');
        row.className = 'product-row';

        // Format the date for the input field (datumVratenia)
        const formattedDatumObjednavky = formatDate(product.datumVratenia);
        row.innerHTML = `
            <td>${product.produkt.idProdukt}</td>
            <td>${product.produkt.nazov}</td>
            <td>${product.datumVypozicania}</td>
            <td class="returnDate">
                <input id="inputDate-${product.id}" type="date" value="${formattedDatumObjednavky}" style="display: ${rola === 'ADMIN' ? 'inline-block' : 'none'};">
            </td>
        `;

        productTab.appendChild(row);

        const inputDate = document.getElementById(`inputDate-${product.id}`);
        const returnDateTd = inputDate.parentElement; // Get the parent td of the input field

        console.info(rola);

        if (rola === "ADMIN") {

            saveButton.style.display = "inline-block";
            // Listen for changes in the input field and store the data
            inputDate.addEventListener("change", (event) => {
                const data = {
                    idObjednavkaProdukt: product.id,
                    datumVratenia: event.target.value
                };
                updateDatumy.push(data);
                saveButton.disabled = false;
            });
        } else {
            // For non-admin users, hide the input field and keep the original date
            returnDateTd.textContent = product.datumVratenia;
        }
    });
}

document.addEventListener("DOMContentLoaded", function () {

    saveButton = document.getElementById('ulozit');


    fetchUser().then(response => {
        let rola = response.rola;

        fetchUsers(rola);
        includeHTML();

    });


    if (sessionStorage.getItem('showToastr') === 'true') {
        toastr.info("Vykonanie zmien bolo zrušené");
        sessionStorage.removeItem('showToastr'); // Vymažeme informáciu z sessionStorage
    }


    if (updateDatumy.length === 0) {
        saveButton.disabled = true;

    }

    saveButton.addEventListener('click', (event) => {
        event.preventDefault();

        const requestData = {
            idObjednavka: orderId,
            produkty: updateDatumy
        }

        if (updateDatumy.length > 0) {
            const isConfirmed = confirm("Ste si istý, že chcete uložiť zmeny?");

            if (isConfirmed) {
                axios.post('http://localhost:8080/objednavka/update-date', requestData, {withCredentials: true}).then(response => {
                    if (response.status === 200) {
                        updateDatumy = [];
                        toastr.success("Zmeny boli úspešne uložené.");

                    }
                }).catch(error => {
                    console.error("Chyba pri ukladaní zmien:", error);
                    toastr.error("Nastala chyba pri ukladaní zmien. Skúste to znova.");
                });
            } else {
                sessionStorage.setItem('showToastr', 'true');
                window.location.reload();


            }
        }
    });
});




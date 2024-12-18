import {createFooter, createHeader, updateCartCount} from './neutralne.js';
import {fetchUser} from "./helpers.js";


let meno;
let rola;
let saveButton;
let updateObjednavka = [];


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

function formatDateToISO(date) {
    const [day, month, year] = date.split('.'); // Rozdeľte dátum na časti
    return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`; // Zložte ISO formát
}

function displayUsers(orders) {
    const orderTab = document.getElementById('orderTable');

    orders.forEach(order => {
        const row = document.createElement('tr');

        row.dataset.orderId = order.idObjednavka;

        meno = order.pouzivatel.meno + " " + order.pouzivatel.priezvisko;

        const formattedDate = formatDateToISO(order.datumVratenia);

        const stavyObjednavky = {
            SCHVALENA: "SCHVÁLENÁ",
            CAKAJUCA: "ČAKAJÚCA",
            ZAMIETNUTA: "ZAMIETNUTÁ",
            POZICANA: "POŽIČANÁ",
            VRATENA: "VRÁTENÁ"
        };



        row.className = 'order-row';

        if (order.stavObjednavky === 'ZAMIETNUTA') {
            row.classList.add('tr-zamietnuta');

        }
        row.innerHTML = `
          <tr>
            <td> <span class="${order.stavObjednavky === 'ZAMIETNUTA' ? 'zamietnuta-span' : ''}">
                    ${order.idObjednavka}
                </span>
                <a id="pdfDocument-${order.idObjednavka}" 
                    class="pdfLink ${order.stavObjednavky === 'ZAMIETNUTA' ? 'hidden' : ''}" 
                    target="_blank" 
                    title="Stiahnuť PDF">
                    <!-- Ikona: Môžete použiť Font Awesome alebo SVG -->
                    <i class="fa-solid fa-file-pdf" style="color: red; font-size: 1.2rem;"></i>
                </a></td>
            <td>${order.datumVypozicania}</td>
             <td class="returnDate">
                <input 
                    id="inputDate-${order.idObjednavka}" 
                    type="date" 
                    value="${formattedDate}" 
                    style="display: ${rola === 'ADMIN' ? 'inline-block' : 'none'};"
                    class="${order.stavObjednavky === 'ZAMIETNUTA' ? 'zamietnuta-span' : ''}"
                    ${order.stavObjednavky !== 'SCHVALENA' && order.stavObjednavky !== 'POZICANA' ? 'readonly' : ''}
                >
            </td>
             <td class="orderStatus">
                <select 
                    class="custom-select ${order.stavObjednavky === 'ZAMIETNUTA' ? 'zamietnuta' : ''}"
                    id="statusSelect-${order.idObjednavka}"
                    ${order.stavObjednavky === 'ZAMIETNUTA' ? 'disabled' : ''}
                >
                    ${Object.keys(stavyObjednavky).map(key => `
                        <option value="${key}" ${key === order.stavObjednavky ? 'selected' : ''}>
                            ${stavyObjednavky[key]}
                        </option>
                    `).join('')}
                </select>
            </td>
          </tr>
        `;


        const returnDateCell = row.querySelector('.returnDate');
        returnDateCell.addEventListener('click', (event) => {
            event.stopPropagation();
        });

        const statusCell = row.querySelector('.orderStatus');
        statusCell.addEventListener('click', (event) => {
            event.stopPropagation();
        });

        const pdfLinks = document.querySelectorAll('.pdfLink');
        pdfLinks.forEach(link => {
            link.addEventListener('click', (event) => {
                event.stopPropagation();
            });
        });

        row.addEventListener('click', (event) => {
            const orderId = row.dataset.orderId;
            window.location.href = `productsOrder.html?orderId=${orderId}`;
        });

        orderTab.appendChild(row);

        const inputDate = document.getElementById(`inputDate-${order.idObjednavka}`);
        const returnDateTd = inputDate.parentElement;
        const inputStavObjednavky = document.getElementById(`statusSelect-${order.idObjednavka}`);
        const returnStavObjednaky = inputStavObjednavky.parentElement;
        const pdfDocument = document.getElementById(`pdfDocument-${order.idObjednavka}`);

        pdfDocument.addEventListener('click', (event)=> {
            event.preventDefault();
            const id = pdfDocument.id.split('pdfDocument-')[1];
            const requestData = {
                idObjednavka: id
            }
            axios.post('http://localhost:8080/objednavka/generate', requestData, {
                withCredentials: true,
                responseType: 'blob' // Dôležité: nastaví typ odpovede na binárne dáta (PDF)
            }).then(response => {
                if (response.status === 200) {
                    // Vytvorenie URL pre PDF súbor
                    const pdfBlob = new Blob([response.data], { type: 'application/pdf' });
                    const pdfUrl = URL.createObjectURL(pdfBlob);

                    // Otvorenie PDF v novom okne alebo náhľad
                    const newWindow = window.open(pdfUrl, '_blank');
                    if (!newWindow) {
                        alert("Pop-up okno bolo blokované. Skontrolujte nastavenia prehliadača.");
                    }

                    // Voliteľné: umožniť stiahnutie PDF
                    const link = document.createElement('a');
                    link.href = pdfUrl;
                    link.click();

                    // Uvoľnenie URL z pamäte
                    URL.revokeObjectURL(pdfUrl);
                }
            }).catch(error => {
                console.error("Chyba pri generovaní PDF:", error);
            });
        });

        if (rola === "ADMIN") {

            saveButton.style.display = "inline-block";

            inputDate.addEventListener("change", (event) => {
                updateOrderInList(order.idObjednavka, event.target.value);
            });

            inputStavObjednavky.addEventListener("change", (event) => {
                console.log(event.target.value)
                updateOrderInList(order.idObjednavka, null, event.target.value);
            });

        } else {
            // For non-admin users, hide the input field and keep the original date
            returnDateTd.textContent = order.datumVratenia;
            returnStavObjednaky.textContent = order.stavObjednavky;

        }


    });
    document.getElementById('orderTitle').textContent += ": " + meno;
}

function updateOrderInList(id, datumVratenia = null, stavObjednavky = null) {
    const index = updateObjednavka.findIndex(order => order.idObjednavka === id);

    if (index !== -1) {
        if (datumVratenia) updateObjednavka[index].datumVratenia = datumVratenia;
        if (stavObjednavky) updateObjednavka[index].stavObjednavky = stavObjednavky;
    } else {
        // Ak objednávka ešte neexistuje, pridáme ju
        updateObjednavka.push({ idObjednavka: id, datumVratenia, stavObjednavky });
    }

    saveButton.disabled = false;
}




document.addEventListener("DOMContentLoaded", function () {
    document.body.insertBefore(createHeader(), document.body.firstChild);
    document.body.insertBefore(createFooter(), document.body.lastChild);
    fetchUser().then(response => {
        rola = response.rola;
    });
    saveButton = document.getElementById('ulozit');

    fetchOrders();
    updateCartCount();

    if (updateObjednavka.length === 0) {
        saveButton.disabled = true;
    }


    saveButton.addEventListener('click', (event) => {
        event.preventDefault();
        console.log(updateObjednavka);


        if (updateObjednavka.length > 0) {
            const isConfirmed = confirm("Ste si istý, že chcete uložiť zmeny?");

            if (isConfirmed) {
                axios.post('http://localhost:8080/objednavka/update-objednavka', updateObjednavka, {withCredentials: true}).then(response => {
                    if (response.status === 200) {
                        updateObjednavka = [];
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
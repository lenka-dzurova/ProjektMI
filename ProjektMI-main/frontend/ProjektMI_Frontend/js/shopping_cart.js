import {createFooter, createHeader, updateCartCount} from './neutralne.js';
const cartDiv = document.getElementById('cart');
const cart = JSON.parse(localStorage.getItem('cart')) || [];

let pouzivatelId;

function renderCart() {

    axios.get("http://localhost:8080/pouzivatel-udaje", {withCredentials: true}).then(response => {
        pouzivatelId = response.data.id;
    });


    cartDiv.innerHTML = ""; // Vyprázdni predchádzajúci obsah
    cart.forEach(item => {
        const cartItemDiv = document.createElement("div");
        cartItemDiv.classList.add("cart-box");
        console.info(item.id);
        cartItemDiv.innerHTML = `
        <div class="cart-item">
            <img src="data:image/jpeg;base64,${item.image}" alt="${item.nazov}" class="cart-item-image">
            <div class="cart-item-details">
                <h3 class="cart-item-title">${item.nazov}</h3>
                <div class="cart-item-dates">
                    <p><strong>Od:</strong> ${item.startDate || 'N/A'}</p>
                    <p><strong>Do:</strong> ${item.endDate || 'N/A'}</p>
                </div>
            </div>
            <button class="remove-btn" data-id="${item.id}" aria-label="Odstrániť položku">
                &times;
            </button>
        </div>

    `;

        cartDiv.appendChild(cartItemDiv);
    });


    cartDiv.querySelectorAll(".remove-btn").forEach(button => {
        button.addEventListener("click", () => {
            const itemId = button.getAttribute("data-id");
            removeFromCart(itemId);
        });
    });
}


function removeFromCart(id) {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    cart = cart.filter(item => item.id !== id); // Odstrániť produkt z košíka
    localStorage.setItem('cart', JSON.stringify(cart)); // Uložiť aktualizovaný košík
    renderCart();
    window.location.reload();
}


// TODO toto je spravna potom odkomentuj // TODO upravit aby to ziskavalo datum z objednavky nie z produktu
// document.getElementById('checkout-btn').addEventListener('click', (event) => {
//     event.preventDefault();
//
//     const objednavkaData = {
//         pouzivatelId: pouzivatelId,
//         objednavkaProduktyDTO: cart.map(product => ({
//             produktId: product.id,
//             datumVypozicania: product.startDate,
//             datumVratenia: product.endDate
//         }))
//     }
//
//     console.log(objednavkaData)
//
//     axios.post('http://localhost:8080/objednavka/vytvor', objednavkaData, {
//         headers: {
//             'Content-Type': 'application/json'
//         },
//         withCredentials: true
//     }).then(response => {
//         if (response.status === 201) {
//             localStorage.clear();
//             window.location.reload();
//         }
//     })
// })


//TODO potom odstran to som spravil aby sa upravilo to hore podla tohto
document.getElementById('checkout-btn').addEventListener('click', (event) => {
    event.preventDefault();

    const objednavkaData = {
        pouzivatelId: pouzivatelId,
        datumVypozicania: cart[0].startDate,
        datumVratenia: cart[0].endDate,
        objednavkaProduktyDTO: cart.map(product => ({
            produktId: product.id
        }))
    }

    console.log(objednavkaData)

    axios.post('http://localhost:8080/objednavka/vytvor', objednavkaData, {
        headers: {
            'Content-Type': 'application/json'
        },
        withCredentials: true
    }).then(response => {
        console.log(response)
        if (response.status === 200) {
            localStorage.clear();
            sessionStorage.setItem('showToastr', 'true');
            window.location.reload();
        }
    })
})



// Načítanie košíka pri načítaní stránky
document.addEventListener('DOMContentLoaded', () => {
    document.body.insertBefore(createHeader(), document.body.firstChild);
    document.body.insertBefore(createFooter(), document.body.lastChild);
    if (sessionStorage.getItem('showToastr') === 'true') {
        toastr.info("Objednávka bolo úspešne vytvorená");
        sessionStorage.removeItem('showToastr'); // Vymažeme informáciu z sessionStorage
    }
    renderCart();
    updateCartCount();
});


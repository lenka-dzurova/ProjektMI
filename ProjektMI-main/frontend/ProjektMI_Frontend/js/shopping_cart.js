const cartDiv = document.getElementById('cart');
const cart = JSON.parse(atob(localStorage.getItem('cart'))) || [];

function renderCart() {

    cartDiv.innerHTML = ""; // Vyprázdni predchádzajúci obsah
    cart.forEach(item => {
        const cartItemDiv = document.createElement("div");
        cartItemDiv.classList.add("cart-item");

        cartItemDiv.innerHTML = `
        <h3>${item.nazov}</h3>
        <p>Od: ${item.startDate}</p>
        <p>Do: ${item.endDate}</p>
        <button class="remove-btn" data-id="${item.id}">Odstrániť</button>
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
    let cart = JSON.parse(atob(localStorage.getItem('cart'))) || [];
    cart = cart.filter(item => item.id !== id); // Odstrániť produkt z košíka
    localStorage.setItem('cart', JSON.stringify(cart)); // Uložiť aktualizovaný košík
    renderCart();
    window.location.reload();
}

document.getElementById('button-remove')


// Načítanie košíka pri načítaní stránky
document.addEventListener('DOMContentLoaded', renderCart);


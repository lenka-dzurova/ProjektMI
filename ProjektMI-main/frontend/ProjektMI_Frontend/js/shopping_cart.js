const cartDiv = document.getElementById('cart');
const cart = JSON.parse(localStorage.getItem('cart')) || [];

function renderCart() {

    cartDiv.innerHTML = ""; // Vyprázdni predchádzajúci obsah
    cart.forEach(item => {
        const cartItemDiv = document.createElement("div");
        cartItemDiv.classList.add("cart-item");

        cartItemDiv.innerHTML = `
        <div class="cart-item">
        <img src="${item.image}" alt="${item.nazov}" class="cart-item-image">
        <div class="cart-item-details">
            <h3 class="cart-item-title">${item.nazov}</h3>
            <div class="cart-item-dates">
                <p><strong>Od:</strong> ${item.startDate || 'N/A'}</p>
                <p><strong>Do:</strong> ${item.endDate || 'N/A'}</p>
            </div>
            <button class="remove-btn" data-id="${item.id}">Odstrániť</button>
        </div>
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

document.getElementById('button-remove')


// Načítanie košíka pri načítaní stránky
document.addEventListener('DOMContentLoaded', renderCart);


import {fetchUser} from "./helpers.js";
let rola;
let cartIcon;
let orderLink;
let cartText;
let orderMobile;
let profilText;

export function createHeader() {
    const div = document.createElement('div');
    div.innerHTML = `
<nav class="navbar navbar-expand-lg">
            <div class="container-fluid">
                <a class="navbar-brand" href="../html/products.html"><img src="../img/logo_uniza2.png" width="200rem" alt="logo"></a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="custom-toggler-icon"><i class="bi bi-list"></i></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNav">
                    <div class="ms-auto d-flex">
                        <!-- User Icon with Dropdown -->
                        <div class="dropdown">
                            <li>
                                <img src="../img/ikony/user.png" width="50" alt="user icon" class="user-icon">
                            </li>
                            <div class="dropdown-content">
                                <a href="#"><i class="fas fa-user"></i> Profil</a>
                                <a href="#" id="orders" width: 100%;"><i class="fas fa-box"></i> Objednávky</a>
                                <a href="#" id="logout"><i class="fas fa-sign-out-alt"></i> Odhlásiť</a>
                            </div>
                        </div>
                    </div>
                    <div class="mobile-dropdown mt-3">
                        <span  id="profilLink">PROFIL</span>
                    </div>
                    <div class="mobile-dropdown-menu">
                        <a href="shopping_cart.html" id="cart">KOŠÍK</a>
                        <a href="#">PROFIL</a>
                        <a href="#" id="ordersMobile" style="display: none">OBJEDNÁVKA</a>
                        <a href="#" id="logoutMobile">ODHLÁSIŤ</a>
                    </div>
                    <!-- Shopping Cart -->
                    <div class="shopping-cart me-3">
                        <a href="shopping_cart.html" id="icon-cart" class="position-relative">
                            <i class="fas fa-shopping-cart"></i>
                            <span id="cart-count">0</span>
                        </a>
                    </div>
                </div>
            </div>
        </nav>`;


    return div;
}





export function createFooter() {
    const footer = document.createElement('footer');
    footer.innerHTML = `
    <div class="footer-container">
        <div class="footer-row">
            <!-- Kontaktné informácie -->
            <div class="footer-column footer-contact">
                <div class="footer-logo bm-3" >
                    <img src="../img/uniza-logo.png" alt="UNIZA Logo">
                </div>
                <h5>ŽILINSKÁ UNIVERZITA V ŽILINE</h5>
                <p>Univerzitná 8215/1</p>
                <p>010 26 Žilina</p>
                <p>+421 415 135 001</p>
            </div>
            
            <!-- Sociálne siete -->
            <div class="footer-column footer-social">
                <h5>Sledujte nás</h5>
                <a href="https://www.facebook.com/uniza.sk?fref=ts" class="social-link">
                    <i class="bi bi-facebook"></i> Facebook
                </a>
                <a href="https://www.instagram.com/zilinska.univerzita/?hl=en" class="social-link">
                    <i class="bi bi-instagram"></i> Instagram
                </a>
                <a href="https://www.youtube.com/channel/UCLE1gf9emVISc6FCmbgSCFA" class="social-link">
                    <i class="bi bi-youtube"></i> YouTube
                </a>
            </div>
        </div>
    </div>
    `;
    return footer;
}



document.addEventListener('DOMContentLoaded', () => {
    fetchUser().then(response => {
        rola = response.rola;

        orderLink = document.getElementById('orders');
        cartIcon = document.getElementById('icon-cart');
        cartText = document.getElementById('cart');
        orderMobile = document.getElementById('ordersMobile');
        profilText = document.getElementById('profilLink');

        function updateDisplay() {
            if (rola === 'ADMIN') {
                orderLink.style.display = 'none';
                orderMobile.style.display = 'none';
            } else {
                orderLink.style.display = 'block';
                if (window.innerWidth <= 990) {

                    orderMobile.style.display = 'block'; // Zobraziť na menších obrazovkách


                } else {
                    orderMobile.style.display = 'none'; // Skryť na väčších obrazovkách
                }
            }
        }

        document.getElementById("logout").addEventListener("click", () => {
            odhlasenie();
        });

        document.getElementById('logoutMobile').addEventListener('click', () => {
            odhlasenie();
        });


        // Počiatočná aktualizácia zobrazenia
        updateDisplay();

        // Sleduj zmenu veľkosti okna
        window.addEventListener('resize', updateDisplay);

        profilText.textContent = response.meno + " " + response.priezvisko;
        profilText.style.textTransform = 'uppercase';

        orderMobile.addEventListener("click",()=>{
            window.location.href = `userOrders.html?id=${response.id}`;
        });

        orderLink.addEventListener("click", function (event) {
            window.location.href = `userOrders.html?id=${response.id}`;
        });

        profilText.addEventListener('click', (event) => {
            const menu = document.querySelector('.mobile-dropdown-menu');
            menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
        });



    })



    const fontAwesomeLink = document.createElement("link");
    fontAwesomeLink.rel = "stylesheet";
    fontAwesomeLink.href = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css";

    // Pridanie do <head>
    document.head.appendChild(fontAwesomeLink);

})



function odhlasenie() {
    axios.post('http://localhost:8080/odhlasenie', null, {withCredentials: true})
        .then(res => {
            if (res.status === 200) {
                localStorage.removeItem("Datumy");
                window.location.href = "index.html";
                console.log('Odhlásenie úspešné'); // Odhlásenie úspešné
            }
        })
        .catch(error => {
            console.error('Chyba pri odhlásení:', error); // Chyba pri odhlásení
        });

}

export async function updateCartCount() {
    const cart = JSON.parse(localStorage.getItem("cart")) || [];
    const cartCountElement = document.getElementById("cart-count"); // Element pre počet produktov

    console.log(cartCountElement)

    const productCount = cart.length; // Získaj počet produktov v košíku

    if (productCount > 0) {
        cartCountElement.textContent = productCount; // Nastav počet produktov
        cartCountElement.style.display = "inline-block"; // Zobraz počet
    } else {
        cartCountElement.style.display = "none"; // Skry, ak je košík prázdny
    }
};
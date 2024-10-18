import {fetchUser} from "./helpers.js";


document.addEventListener("DOMContentLoaded", function() {


    fetchUser().then(response => {
        console.log("User outside async function (from global variable):", response);
        // Teraz môžeš použiť údaje z `userData` kdekoľvek
    });
    // Keď kliknete na "AUDIO TECHNOLÓGIE"
    const heading = document.getElementById("section-heading");
    document.getElementById('audioLink').addEventListener('click', function(e) {
        e.preventDefault(); // Zamedzíme default akcii odkazu
        document.getElementById('audioSection').style.display = 'block'; // Zobrazíme audio sekciu
        document.getElementById('videoSection').style.display = 'none';// Skryjeme video sekciu
        heading.textContent = 'NAŠE AUDIO TECHNOLÓGIE';
    });

    // Keď kliknete na "VIDEO TECHNOLÓGIE"
    document.getElementById('videoLink').addEventListener('click', function(e) {
        e.preventDefault();
        document.getElementById('videoSection').style.display = 'block'; // Zobrazíme video sekciu
        document.getElementById('audioSection').style.display = 'none';  // Skryjeme audio sekciu
        heading.textContent = 'NAŠE VIDEO TECHNOLÓGIE';
    });

    document.getElementById("logout").addEventListener("click", () => {
        axios.post('http://localhost:8080/odhlasenie', null, { withCredentials: true })
            .then(res => {
                if (res.status === 200) {
                    window.location.href = "index.html";
                    console.log('Odhlásenie úspešné'); // Odhlásenie úspešné
                }
            })
            .catch(error => {
                console.error('Chyba pri odhlásení:', error); // Chyba pri odhlásení
            });
    });



    // Zobrazenie náhľadu obrázka
    document.getElementById("productImage").addEventListener("change", function(event) {
        var file = event.target.files[0];
        var reader = new FileReader();

        reader.onload = function(e) {
            var imagePreview = document.getElementById("imagePreview");
            var clearImageBtn = document.getElementById("clearImageBtn");

            imagePreview.src = e.target.result;
            imagePreview.style.display = "block";
            clearImageBtn.style.display = "block"; // Zobraz krížik
        };

        if (file) {
            reader.readAsDataURL(file);
        }
    });

    document.getElementById("clearImageBtn").addEventListener("click", function(event) {
        event.preventDefault();
        var imagePreview = document.getElementById("imagePreview");
        var productImageInput = document.getElementById("productImage");
        var clearImageBtn = document.getElementById("clearImageBtn");

        // Skryť náhľad obrázka
        imagePreview.style.display = "none";
        imagePreview.src = ""; // Resetovať zdroj obrázka
        clearImageBtn.style.display = "none"; // Skryť krížik

        // Resetovať vstup pre obrázok
        productImageInput.value = "";
    });




    document.getElementById("saveProductBtn").addEventListener("click", function () {
        const id = document.getElementById('productId').value;
        const name = document.getElementById('productName').value;
        const info = document.getElementById('productInfo').value;
        const type = document.getElementById('productType').value;
        const count = document.getElementById('productCount').value;
        const image = document.getElementById('productImage').files[0]; // Use .files[0] to get the first file

        // Validate fields
        if (!name || !info || !type || !count || !image) {
            console.error("All fields must be filled out");
            return; // Exit if validation fails
        }

        // Create a FormData object
        const formData = new FormData();
        formData.append('id', id);
        formData.append('nazov', name);
        formData.append('popis', info);
        formData.append('obrazok', image); // Add the image file
        formData.append('typTechniky', type);
        formData.append('pocetKusov', count);

        axios.post('http://localhost:8080/produkt/pridat', formData, {
            withCredentials: true,
            headers: {
                'Content-Type': 'multipart/form-data', // Set the correct content type for form data
            }

        }).then(response => {
            console.log(response.status);
            if (response.status === 201) {
                location.reload();
            }
        }).catch(error => {
            console.error(error);
            if (error.response) {
                console.error('Server responded with:', error.response.data);
            }
        });
    });
});

// Načítanie produktov z API
async function fetchProducts() {
    try {
        const response = await axios.get('http://localhost:8080/produkt/get-all', {withCredentials: true});
        const products = response.data;

        const audioProducts = products.filter(product => product.typTechniky === 'audio');
        const videoProducts = products.filter(product => product.typTechniky === 'video');

        displayAudioProducts(audioProducts);
        displayVideoProducts(videoProducts);
    } catch (error) {
        console.error('Error fetching products:', error);
        if (error.response) {
            console.error('Server responded with:', error.response.data);
        }
    }
}

function displayAudioProducts(products) {
    const audioSection = document.getElementById('audioSection');
    const audioContainer = audioSection.querySelector('.container');
    const productRow = audioContainer.querySelector('#audioProductsRow');

    products.forEach(product => {
        const col = document.createElement('div');
        col.className = 'col-md-4 mt-3 product-container';
        col.innerHTML = `
            <div class="checkbox-container">
                <input type="checkbox" class="product-checkbox" value="${product.id}">
            </div>
            <a href="product_detail.html?id=${product.id}">
                <div class="big-img">
                    <img src="data:image/jpeg;base64,${product.obrazok}" class="obrazky" alt="${product.nazov}">
                </div>
                <H4 class="nadpis">${product.nazov}</H4>
                <div class="row">
                    <div class="col-md-12 py-2">
                        <H5>${product.popis}</H5>
                    </div>
                </div>
            </a>
        `;
        productRow.appendChild(col);
    });
}

function displayVideoProducts(products) {
    const videoSection = document.getElementById('videoSection');
    const videoContainer = videoSection.querySelector('.container');
    const productRow = videoContainer.querySelector('#videoProductsRow');

    products.forEach(product => {
        const col = document.createElement('div');
        col.className = 'col-md-4 mt-3 product-container';
        col.innerHTML = `
            <div class="checkbox-container">
                <input type="checkbox" class="product-checkbox" value="${product.id}">
            </div>
            <a href="product_detail.html?id=${product.id}">
                <div class="big-img">
                    <img src="data:image/jpeg;base64,${product.obrazok}" class="obrazky" alt="${product.nazov}">
                </div>
                <H4 class="nadpis">${product.nazov}</H4>
                <div class="row">
                    <div class="col-md-12 py-2">
                        <H5>${product.popis}</H5>
                    </div>
                </div>
            </a>
        `;
        productRow.appendChild(col);
    });
}
// Volanie funkcie na načítanie produktov po načítaní stránky
document.addEventListener('DOMContentLoaded', fetchProducts);

let vybraneProdukty = [];

// Event listener pre checkboxy produktov
document.addEventListener('change', (e) => {
    if (e.target.classList.contains('product-checkbox')) {
        const produktId = e.target.value;

        if (e.target.checked) {
            // Pridá ID produktu do poľa vybraneProdukty, ak je zaškrtnutý
            vybraneProdukty.push(produktId);
        } else {
            // Odstráni ID produktu z poľa vybraneProdukty, ak je odškrtnutý
            vybraneProdukty = vybraneProdukty.filter(id => id !== produktId);
        }
    }
});

document.getElementById('deleteSelectedBtn').addEventListener('click', () => {
    if (vybraneProdukty.length > 0) {
        // Zobraz konfirmáciu používateľovi
        const potvrditVymazanie = confirm('Naozaj chcete vymazať vybrané produkty?');

        if (potvrditVymazanie) {
            // Ak používateľ klikol na "OK", pokračujeme s mazaním
            vybraneProdukty.forEach(produktId => {
                axios.delete(`http://localhost:8080/produkt/delete/${produktId}`,null , {withCredentials: true})
                    .then(response => {
                        console.info(response);
                        if (response.status === 200) {
                            location.reload();
                        } else {
                            console.error('Chyba pri mazaní produktu s ID:', produktId);
                        }
                    })
                    .catch(error => {
                        console.error('Chyba pri pripojení k serveru alebo mazaní produktu:', error);
                    });
            });
            // location.reload(); // Obnoví stránku po vymazaní
            vybraneProdukty = [];   
        } else {
            // Ak používateľ klikol na "Cancel", len zatvorí okno a nič sa nestane
            console.log('Mazanie produktov bolo zrušené.');
        }
    } else {
        alert('Nie sú vybrané žiadne produkty na vymazanie.');
    }
});


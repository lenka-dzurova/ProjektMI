import {fetchUser} from "./helpers.js";

let rola = '';
const btnAdd = document.getElementById("addProductBtn");
const btnDelete = document.getElementById("deleteSelectedBtn");
const btnUpdate = document.getElementById("updateProductBtn");
const btnSave = document.getElementById("saveProductBtn");
const btnEdit = document.getElementById("editProductBtn");

const id = document.getElementById('productId');
const name = document.getElementById('productName');
const info = document.getElementById('productInfo');
const type = document.getElementById('productType');
const image = document.getElementById('productImage');
const imagePreview = document.getElementById('imagePreview');

document.addEventListener("DOMContentLoaded", function () {

    fetchUser().then(response => {
        rola = response.rola;

        if (rola === "ADMIN") {
            btnAdd.style.display = "inline-block";
            btnDelete.style.display = "inline-block";
            btnUpdate.style.display = "inline-block";
        }

        fetchProducts(rola);
    });


    btnAdd.addEventListener("click", () => {
        id.value = "";
        name.value = "";
        info.value = "";
        type.value = "";
        image.value = "";
        imagePreview.src = "";
        imagePreview.style.display = 'none';
        clearImageBtn.style.display = 'none';
        btnSave.style.display = "block";
        btnEdit.style.display = "none";
    });


    // Keď kliknete na "AUDIO TECHNOLÓGIE"
    const heading = document.getElementById("section-heading");
    document.getElementById('audioLink').addEventListener('click', function (e) {
        e.preventDefault(); // Zamedzíme default akcii odkazu
        document.getElementById('audioSection').style.display = 'block'; // Zobrazíme audio sekciu
        document.getElementById('videoSection').style.display = 'none';// Skryjeme video sekciu
        heading.textContent = 'NAŠE AUDIO TECHNOLÓGIE';
    });

    // Keď kliknete na "VIDEO TECHNOLÓGIE"
    document.getElementById('videoLink').addEventListener('click', function (e) {
        e.preventDefault();
        document.getElementById('videoSection').style.display = 'block'; // Zobrazíme video sekciu
        document.getElementById('audioSection').style.display = 'none';  // Skryjeme audio sekciu
        heading.textContent = 'NAŠE VIDEO TECHNOLÓGIE';
    });

    document.getElementById("logout").addEventListener("click", () => {
        axios.post('http://localhost:8080/odhlasenie', null, {withCredentials: true})
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
    document.getElementById("productImage").addEventListener("change", function (event) {
        var file = event.target.files[0];
        var reader = new FileReader();

        reader.onload = function (e) {
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

    document.getElementById("clearImageBtn").addEventListener("click", function (event) {
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


    btnSave.addEventListener("click", function () {
        // Use .files[0] to get the first file
        let isValid = true;

        // Validate fields
        if (!name) {
            document.getElementById('productName-error').textContent = 'Názov je povinný';
            document.getElementById('productName').classList.add('invalid');
            isValid = false;
        }

        if (!info) {
            document.getElementById('productInfo-error').textContent = 'Informácie o produkte sú povinné';
            document.getElementById('productInfo').classList.add('invalid');
            isValid = false;
        }

        if (!type) {
            document.getElementById('productType-error').textContent = 'Typ produktu je povinný';
            document.getElementById('productType').classList.add('invalid');
            isValid = false;
        }

        if (!image) {
            document.getElementById('productImage-error').textContent = 'Obrázok je povinný';
            document.getElementById('productImage').classList.add('invalid');
            isValid = false;
        }

        if (isValid) {
            // Create a FormData object
            const formData = new FormData();
            formData.append('id', id.value);
            formData.append('nazov', name.value);
            formData.append('popis', info.value);
            formData.append('obrazok', image.files[0]);
            formData.append('typTechniky', type.value);

            axios.post('http://localhost:8080/produkt/pridat', formData, {
                withCredentials: true,
                headers: {
                    'Content-Type': 'multipart/form-data',
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
        }

    });
});

// Načítanie produktov z API
async function fetchProducts(rolaPouzivatel) {
        console.log(rolaPouzivatel)
        const requestBody = {
            rolaProduktu: rolaPouzivatel
        }

        console.log(requestBody)

        await axios.post(`http://localhost:8080/produkt/get-all-by-rola`, requestBody, {
            headers: {
                "Content-Type": "application/json",
            },
            withCredentials: true
        }).then(response => {
            const products = response.data;

            const audioProducts = products.filter(product => product.typTechniky === 'audio');
            const videoProducts = products.filter(product => product.typTechniky === 'video');

            displayAudioProducts(audioProducts);
            displayVideoProducts(videoProducts);
        }).catch(error => {
            console.error('Error fetching products:', error);
            if (error.response) {
                console.error('Server responded with:', error.response.data);
            }
        });
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
                <input type="checkbox" class="product-checkbox" value="${product.idProdukt}" style="${rola === 'ADMIN' ? 'display: line;' : 'display: none;'}">
            </div>
            <a href="product_detail.html?id=${product.idProdukt}">
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
                <input type="checkbox" id="checkbox" class="product-checkbox" value="${product.idProdukt}" style="${rola === 'ADMIN' ? 'display: line;' : 'display: none;'}">
            </div>
            <a href="product_detail.html?id=${product.idProdukt}">
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

btnDelete.addEventListener('click', () => {
    if (vybraneProdukty.length > 0) {
        // Zobraz konfirmáciu používateľovi
        const potvrditVymazanie = confirm('Naozaj chcete vymazať vybrané produkty?');

        if (potvrditVymazanie) {
            axios.delete('http://localhost:8080/produkt/delete-produkty', {
                data: vybraneProdukty,
                withCredentials: true
            })
                .then(response => {
                    if (response.status === 200) {
                        location.reload();
                    } else {
                        console.error('Chyba pri mazaní produktu s ID:', produktId);
                    }
                }).catch(error => {
                console.error('Chyba pri pripojení k serveru alebo mazaní produktu:', error);
            });
        }
    } else {
        alert('Nie sú vybrané žiadne produkty na vymazanie.');
    }
});

btnUpdate.addEventListener("click",() => {

    if (vybraneProdukty.length === 1) {
        let idProduct = vybraneProdukty[0];
        const productModal = new bootstrap.Modal(document.getElementById("productModal"));

        const requestData = {
            idProdukt : idProduct
        }

        axios.post(`http://localhost:8080/produkt/get-produkt`, requestData, {withCredentials: true})
            .then(response => {
                const product = response.data;
                console.info(product);

                id.value = product.idProdukt;
                name.value = product.nazov;
                info.value = product.popis;
                type.value = product.typTechniky;


                if (product.obrazok) {
                    imagePreview.src = "data:image/jpeg;base64," + product.obrazok; // Zobrazí náhľad obrázka
                    imagePreview.style.display = 'block'; // Zobrazí element pre náhľad
                    document.getElementById('clearImageBtn').style.display = 'block'; // Zobrazí tlačidlo na odstránenie obrázka
                    console.info(image.placeholder.value);

                } else {
                    imagePreview.style.display = 'none';
                    document.getElementById('clearImageBtn').style.display = 'none';
                }
                btnSave.style.display = "none";
                btnEdit.style.display = "block";
                productModal.show();
            })
            .catch(error => console.error('Error fetching product details:', error));
    } else {

        toastr.error('Máte vybratých viac produktov ale môžete mať iba jeden');
    }
});

btnEdit.addEventListener("click",() => {
    // Získajte ID produktu
    const idProduct = id.value;

    // Pripravte JSON produkt
    const updatedProduct = {
        nazov: name.value,
        popis: info.value,
        typTechniky: type.value
    };

    // Vytvorte FormData objekt na odoslanie obrázka, ak je prítomný
    const formData = new FormData();
    formData.append("idProdukt", idProduct)

    formData.append("produktJSON", JSON.stringify(updatedProduct));
    // Zmeňte na ID vášho inputu pre obrázok

    if (image.files.length > 0) {
        formData.append("obrazok", image.files[0]); // Pridajte súbor, ak je vybraný
    }

    // Pošlite PUT požiadavku na aktualizáciu produktu
    axios.put('http://localhost:8080/produkt/update', formData, {
        withCredentials: true,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
        .then(response => {
            console.log('Produkt bol úspešne aktualizovaný:', response.data);
            // Môžete pridať kód na uzavretie modálneho okna alebo na zobrazenie správy o úspechu
        })
        .catch(error => {
            if (error.response) {
                // Server responded with a status other than 200 range
                console.error('Error response:', error.response.data);
                console.error('Error status:', error.response.status);
            } else if (error.request) {
                // Request was made but no response was received
                console.error('Error request:', error.request);
            } else {
                // Something happened in setting up the request
                console.error('Error message:', error.message);
            }
        });
});
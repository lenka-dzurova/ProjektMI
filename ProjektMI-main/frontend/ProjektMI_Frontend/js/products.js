import {fetchUser} from "./helpers.js";

let rola = '';
let aktivneAudio = true;
let aktivneVideo = false;
const btnAdd = document.getElementById("addProductBtn");
const btnDelete = document.getElementById("deleteSelectedBtn");
const btnUpdate = document.getElementById("updateProductBtn");
const btnSave = document.getElementById("saveProductBtn");
const btnEdit = document.getElementById("editProductBtn");
const usersLink = document.getElementById("usersList");

const id = document.getElementById('productId');
const name = document.getElementById('productName');
const info = document.getElementById('productInfo');
const type = document.getElementById('productType');
const image = document.getElementById('productImage');
const imagePreview = document.getElementById('imagePreview');
const status = document.getElementById('productStatus');
const productRola = document.getElementById('productRola');
const searchBar = document.getElementById("searchBar");


document.addEventListener("DOMContentLoaded", function () {
    if (localStorage.getItem('updateProduct') === 'true') {
        toastr.success('Produkt bol úspešne aktualizovaný');
        localStorage.removeItem('updateProduct');
    }

    fetchUser().then(response => {
        rola = response.rola;

        if (rola === "ADMIN") {
            btnAdd.style.display = "inline-block";
            btnDelete.style.display = "inline-block";
            btnUpdate.style.display = "inline-block";
            usersLink.style.display = "inline";
        } else {
            let orders = document.getElementById("orders");

            orders.style.display = "inline-block";
            orders.addEventListener("click", function (event) {
                window.location.href = `userOrders.html?userId=${response.id}`;
            })
        }

        searchBar.addEventListener('input', (event) => {
            liveSearch(rola);
        });


        fetchProducts(rola);
    });


    btnAdd.addEventListener("click", () => {
        id.value = "";
        name.value = "";
        info.value = "";
        type.value = "";
        image.value = "";
        imagePreview.src = "";
        status.value = "";
        imagePreview.style.display = 'none';
        clearImageBtn.style.display = 'none';
        btnSave.style.display = "block";
        btnEdit.style.display = "none";
        id.readOnly = false;
    });


    // Keď kliknete na "AUDIO TECHNOLÓGIE"
    const heading = document.getElementById("section-heading");
    document.getElementById('audioLink').addEventListener('click', function (e) {
        e.preventDefault(); // Zamedzíme default akcii odkazu
        document.getElementById('audioSection').style.display = 'block'; // Zobrazíme audio sekciu
        document.getElementById('videoSection').style.display = 'none';// Skryjeme video sekciu
        heading.textContent = 'NAŠE AUDIO TECHNOLÓGIE';
        aktivneAudio = true;
        aktivneVideo = false;
        fetchProducts(rola);
    });

    // Keď kliknete na "VIDEO TECHNOLÓGIE"
    document.getElementById('videoLink').addEventListener('click', function (e) {
        e.preventDefault();
        document.getElementById('videoSection').style.display = 'block'; // Zobrazíme video sekciu
        document.getElementById('audioSection').style.display = 'none';  // Skryjeme audio sekciu
        heading.textContent = 'NAŠE VIDEO TECHNOLÓGIE';
        aktivneVideo = true;
        aktivneAudio = false;

        fetchProducts(rola);
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

        if (!status) {
            document.getElementById('productStatus-error').textContent = 'Status produktu je povinný';
            document.getElementById('productStatus').classList.add('invalid');
            isValid = false;
        }

        if (!productRola) {
            document.getElementById('productRola-error').textContent = 'Rola produktu je povinná';
            document.getElementById('productRola').classList.add('invalid');
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
            formData.append('stavProduktu', status.value);
            formData.append('rolaProduktu', productRola.value);

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
        let type = 'AUDIO';
        if (aktivneVideo) {
            type = 'VIDEO';
        }
        const requestBody = {
            rolaProduktu: rolaPouzivatel,
            typTechniky: type
        }



        await axios.post(`http://localhost:8080/produkt/get-all-by-rola`, requestBody, {
            headers: {
                "Content-Type": "application/json",
            },
            withCredentials: true
        }).then(response => {
            const products = response.data;


            displayProducts(products);
        }).catch(error => {
            console.error('Error fetching products:', error);
            if (error.response) {
                console.error('Server responded with:', error.response.data);
            }
        });
    }

let section;
let container;
let productRow;
let searchTimeout;

async function liveSearch(rolaProduct) {
    const query = searchBar.value;
    let type = 'AUDIO';
    if (aktivneVideo) {
        type = 'VIDEO';

    }
    if (query.trim() === "") {
        console.log(aktivneVideo);
        if (aktivneVideo) {
            fetchProducts(rolaProduct);

        } else {
            fetchProducts(rolaProduct);
        }
        return;
    }




    const requestBody = {
        rolaProduktu: rolaProduct,
        nazov: query,
        typTechniky: type
    }

    // Zastavíme predchádzajúce požiadavky a počkáme pred odoslaním novej
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(async () => {

        try {

            // Posielame požiadavku na backend s aktuálnym textom v search bar cez axios
            axios.post('http://localhost:8080/produkt/vyhladavanie',requestBody, {
                withCredentials: true
            })
                .then(response => {

                    if (response.status === 200) {
                        const products = response.data;
                        displayProducts(products);
                    }
                }).catch(error => {
                console.error("Chyba pri live search:", error);
            });


        } catch (error) {
            console.error("Chyba pri live search:", error);
        }
    }, 300); // Počkajte 300 ms pred odoslaním požiadavky
}

function displayProducts(products) {

    if (aktivneVideo) {
        section = document.getElementById('videoSection');
        container = section.querySelector('.container');
        productRow = container.querySelector('#videoProductsRow');
    } else {
        section = document.getElementById('audioSection');
        container = section.querySelector('.container');
        productRow = container.querySelector('#audioProductsRow');
    }

    productRow.innerHTML = '';

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
                    <div class="col-md-12 status-text-container" id="status-text" style="${rola === 'ADMIN' ? 'display: line;' : 'display: none;'}; text-align: right" >
                        <H5 id="statusText">${textDesign(product.stavProduktu)}</H5>
                    </div>
                </div>
            </a>
        `;
        const statusTextElement = col.querySelector('#statusText');
        if (product.stavProduktu === "FUNKCNE") {
            statusTextElement.style.color = 'green';
        } else {
            statusTextElement.style.color = 'red';
        }
        productRow.appendChild(col);
    });
}

function textDesign(text) {
    return text === "FUNKCNE" ? 'FUNKČNÉ' : 'NEFUNKČNÉ';
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

                id.value = product.idProdukt;
                name.value = product.nazov;
                info.value = product.popis;
                type.value = product.typTechniky;
                status.value = product.stavProduktu;
                productRola.value = product.rolaProduktu;


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
                id.readOnly = true;
                productModal.show();
            })
            .catch(error => console.error('Error fetching product details:', error));
    } else if (vybraneProdukty.length === 0) {
        toastr.error('Nemáte vybraný žiaden produkt');
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
        typTechniky: type.value,
        stavProduktu: status.value,
        rolaProduktu: productRola.value
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
            if (response.status === 200) {
                localStorage.setItem("updateProduct",true);
                location.reload();
            }
            // Môžete pridať kód na uzavretie modálneho okna alebo na zobrazenie správy o úspechu
        })
        .catch(error => {
            toastr.error('Produkt nebol aktualizovaný');
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
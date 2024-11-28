import {createFooter, createHeader, updateCartCount} from "./neutralne.js";

// Získanie parametrov z URL
const params = new URLSearchParams(window.location.search);
const productId = params.get('id');
let cart = JSON.parse(localStorage.getItem('cart')) || [];
let product;

const requestData = {
    idProdukt: productId
}


//TODO POTOM SA SEM VRAT A SKONTROLUJ CI TO JE DOBRE
document.addEventListener("DOMContentLoaded", () => {
    document.body.insertBefore(createHeader(), document.body.firstChild);
    document.body.insertBefore(createFooter(), document.body.lastChild);

    if (sessionStorage.getItem('showToastr') === 'true') {
        toastr.info("Produkt bol pridaný do košíka");
        sessionStorage.removeItem('showToastr'); // Vymažeme informáciu z sessionStorage
    }


    axios.post('http://localhost:8080/produkt/get-produkt', requestData, {withCredentials: true})
        .then(response => {
            product = response.data;
            // Naplnenie HTML elementov detailmi produktu
            document.querySelector('.big-img img').src = `data:image/jpeg;base64,${product.obrazok}`;
            document.querySelector('h2').textContent = product.nazov;
            document.querySelector('h6').textContent = product.popis;
            document.getElementById("title").textContent = product.nazov;
            vratDatumyObjednavok(productId);
            vratDatumyZKosika(productId);

        })
        .catch(error => console.error('Error fetching product details:', error));


    window.addEventListener('popstate', () => {
        updateCartCount();
    })
    const datumy = JSON.parse(localStorage.getItem('Datumy'));
    const dateStart = new Date(datumy[0]);
    const dateEnd = new Date(datumy[1]);
    startDateElement.textContent = dateStart.toLocaleDateString('sk-SK');
    endDateElement.textContent = dateEnd.toLocaleDateString('sk-SK');

    updateCartCount();
})



const calendarElement = document.getElementById("calendar");
const startDateElement = document.getElementById("start-date").querySelector("span");
const endDateElement = document.getElementById("end-date").querySelector("span");
const monthYearElement = document.getElementById("month-year");
const prevButton = document.getElementById("prev");
const nextButton = document.getElementById("next");



let shoppingCardDates = [];
let reservedDates = [];
let currentMonth = new Date().getMonth();
let currentYear = new Date().getFullYear();


function generateCalendar() {
    calendarElement.innerHTML = ""; // Vyprázdni existujúci kalendár
    monthYearElement.textContent = `${getMonthName(currentMonth)} ${currentYear}`; // Nastaví názov mesiaca

    const firstDay = new Date(currentYear, currentMonth, 1).getDay(); // Získa prvý deň v mesiaci
    const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate(); // Získa počet dní v mesiaci

    // Pridá prázdne divy pre dni pred prvým dňom
    for (let i = 0; i < firstDay; i++) {
        const emptyDiv = document.createElement("div");
        emptyDiv.className = "day"; // Trieda pre deň
        calendarElement.appendChild(emptyDiv);

    }

    // Pridá dni do kalendára
    for (let day = 1; day <= daysInMonth; day++) {
        const dayElement = document.createElement("div");
        dayElement.className = "date"; // Trieda pre dátum
        dayElement.textContent = day;

        const dateValue = new Date(currentYear, currentMonth, day+1).toISOString().split("T")[0];
        dayElement.setAttribute("data-date", dateValue);
        dayElement.style.pointerEvents = "none";

        // Check if the date is reserved
        if (reservedDates.includes(dateValue)) {
            dayElement.classList.add("reserved");
            dayElement.classList.add("disabled");

        }


        if (shoppingCardDates.includes(dateValue)) {
            dayElement.classList.add("selectedCard");
            dayElement.classList.add("disabled");
        }




        calendarElement.appendChild(dayElement); // Pridá deň do kalendára
    }
}




function getMonthName(monthIndex) {
    const monthNames = [
        "Január", "Február", "Marec", "Apríl",
        "Máj", "Jún", "Júl", "August",
        "September", "Október", "November", "December"
    ];
    return monthNames[monthIndex]; // Vráti názov mesiaca podľa indexu
}

prevButton.addEventListener("click", () => {
    currentMonth--;
    if (currentMonth < 0) {
        currentMonth = 11;
        currentYear--;
    }
    generateCalendar();
});

nextButton.addEventListener("click", () => {
    currentMonth++;
    if (currentMonth > 11) {
        currentMonth = 0;
        currentYear++;
    }
    generateCalendar();
});

generateCalendar();


let isProductInCart = false;
document.getElementById('objednat').addEventListener('click', function () {

    if (startDateElement.textContent != null && endDateElement.textContent != null) {
        // let cart = JSON.parse(localStorage.getItem('cart')) || [];
        // Pridáme nový produkt s dátumami do košíka
        if (cart.length === 0) {
            console.log('Košík je prázdny');
        } else {
            console.log('Košík obsahuje produkty');
        }
        if (cart.length === 0) {
            updateCart();
            sessionStorage.setItem('showToastr', 'true');
            window.location.reload();
        } else {

            if (cart[0].startDate === startDateElement.textContent && cart[0].endDate === endDateElement.textContent) {
                for (let i = 0; i < cart.length; i++ ) {
                    if (cart[i].id === product.idProdukt) {
                        isProductInCart = true;
                        break;
                    }
                }
                if (isProductInCart) {
                    toastr.error('Tento produkt sa už nachádza v košíku.');
                } else {
                    updateCart();
                    sessionStorage.setItem('showToastr', 'true');
                    window.location.reload();
                }

            } else {
                toastr.error("V objednávke musia byť rovnaké dátumy vypožičania a vrátenia všetkých produktov");
            }

        }





    } else {
        toastr.error("Nevybrali ste dátumy, kedy chcete rezevovať techniku")
    }

});


document.getElementById("calendarClose").addEventListener("click", () => {
    generateCalendar();
});

function updateCart() {
    cart.push({
        id: product.idProdukt,
        nazov: product.nazov,
        image: product.obrazok,
        startDate: startDateElement.textContent ? startDateElement.textContent : null,
        endDate: endDateElement.textContent ? endDateElement.textContent : null
    });
    // Uložíme aktualizovaný zoznam do localStorage
    updateCartCount();
    localStorage.setItem('cart', JSON.stringify(cart));
}


function vratDatumyObjednavok(idProdukt) {
    const requestData = {
        idProdukt: idProdukt
    }

    axios.post('http://localhost:8080/objednavka/datumy-objednavok', requestData, {
        headers: {
            'Content-Type': 'application/json'
        },
        withCredentials: true
    }).then(response => {
        if (response.status === 200) {
            const rezervacie = response.data;
            reservedDates = []; // Reset reserved dates array
            // Process each reservation to populate reservedDates
            rezervacie.forEach(rezervacia => {
                const startDateBooking = new Date(rezervacia.datumVypozicania.split('. ').reverse().join('-'));
                const endDateBooking = new Date(rezervacia.datumVratenia.split('. ').reverse().join('-'));

                let currentDate = new Date(startDateBooking);
                endDateBooking.setDate(endDateBooking.getDate() + 1);
                while (currentDate < endDateBooking) {

                     // Move to the next day

                    reservedDates.push(currentDate.toISOString().split("T")[0]);
                    currentDate.setDate(currentDate.getDate() + 1);


                }
            });

            generateCalendar();
        }
    });

}

function vratDatumyZKosika(idProdukt) {
    cart.forEach(item => {
        if (item.id === idProdukt) {
            const startDateBooking = new Date(item.startDate.split('. ').reverse().join('-'));
            const endDateBooking = new Date(item.endDate.split('. ').reverse().join('-'));

            let currentDate = new Date(startDateBooking);
            endDateBooking.setDate(endDateBooking.getDate() + 1);
            while (currentDate < endDateBooking) {
                shoppingCardDates.push(currentDate.toISOString().split("T")[0]);
                currentDate.setDate(currentDate.getDate() + 1); // Move to the next day
            }
        }
    });
}



// window.onfocus = function() {
//     // Ak je stránka vrátená do popredia, obnov stránku
//     window.location.reload();
// };

// Získanie parametrov z URL
const params = new URLSearchParams(window.location.search);
const productId = params.get('id');
let cart = JSON.parse(localStorage.getItem('cart')) || [];
let product;

const requestData = {
    idProdukt: productId
}

document.addEventListener("DOMContentLoaded", () => {



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


    updateCartCount();
})



const calendarElement = document.getElementById("calendar");
const startDateElement = document.getElementById("start-date").querySelector("span");
const endDateElement = document.getElementById("end-date").querySelector("span");
const monthYearElement = document.getElementById("month-year");
const prevButton = document.getElementById("prev");
const nextButton = document.getElementById("next");
const confirmButton = document.getElementById("confirmButton");
const productModal = new bootstrap.Modal(document.getElementById("productModal"));

let shoppingCardDates = [];
let reservedDates = [];
let selectedDays = [];
let currentMonth = new Date().getMonth();
let currentYear = new Date().getFullYear();
let startDate = null; // Počiatočný dátum
let endDate = null; // Koncový dátum

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


        // Check if the date is reserved
        if (reservedDates.includes(dateValue)) {
            dayElement.classList.add("reserved");
            dayElement.classList.add("disabled");
            dayElement.style.pointerEvents = "none";
        }


        if (shoppingCardDates.includes(dateValue)) {
            dayElement.classList.add("selectedCard");
            dayElement.classList.add("disabled");
            dayElement.style.pointerEvents = "none";
        }

        // Ak je deň vybraný, nastavíme ho ako selected
        if (selectedDays.includes(dateValue)) {
            dayElement.classList.add("selected");
        }

        // Pridá event listener na kliknutie na deň
        dayElement.addEventListener("click", () => toggleSelection(new Date(currentYear, currentMonth, day), dayElement));

        calendarElement.appendChild(dayElement); // Pridá deň do kalendára
    }
}

function resetSelection() {
    selectedDays = [];
    startDate = null;
    endDate = null;
    document.querySelectorAll(".date").forEach(element => element.classList.remove("selected", "start-date", "end-date"));
}

function updateSelectedDates() {
    if (startDate) {
        startDateElement.textContent = startDate.toLocaleDateString();
    } else {
        startDateElement.textContent = "";
    }
    if (endDate) {
        endDateElement.textContent = endDate.toLocaleDateString();
    } else {
        endDateElement.textContent = "";
    }
}

// Funkcia na toggle výberu
function toggleSelection(selectedDate, element) {
    const dateString = selectedDate.toDateString();

    if (!startDate || (startDate && endDate)) {
        // Resetuje výber a nastaví nový počiatočný dátum
        resetSelection();
        startDate = selectedDate;
        element.classList.add("start-date");
    } else if (!endDate) {
        // Nastaví koncový dátum a vyberie rozsah
        endDate = selectedDate;
        element.classList.add("end-date");
        // Ak je počiatočný dátum po koncovom, prehodí ich
        if (endDate < startDate) {
            [startDate, endDate] = [endDate, startDate];
        }

        selectRange(startDate, endDate);

    }

    updateSelectedDates(); // Aktualizácia zobrazenia dátumov
}


function selectRange(startDate, endDate) {
    let currentDate = new Date(Date.UTC(startDate.getFullYear(), startDate.getMonth(), startDate.getDate()));
    const lastDate = new Date(endDate);

    // Prejdeme dni medzi počiatočným a koncovým dňom, aj cez rôzne mesiace
    while (currentDate <= lastDate) {

        const dateString = currentDate.toISOString().split("T")[0]; // Formátovanie dátumu na YYYY-MM-DD

        selectedDays.push(dateString);

        // Vyhľadanie elementu podľa data-date a označenie
        const dayElement = document.querySelector(`.date[data-date="${dateString}"]`);

        if (dayElement) {
            dayElement.classList.add("selected");
        }

        currentDate.setDate(currentDate.getDate() + 1); // Posun na ďalší deň
    }
    startDateElement.textContent = startDate.toLocaleDateString();
    endDateElement.textContent = endDate.toLocaleDateString();

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

confirmButton.addEventListener("click", () => {

    if (selectedDays.length > 0 && (startDate && endDate)) {
        productModal.hide();
    } else {
        startDateElement.textContent = "";
        endDateElement.textContent = "";
        toastr.error('Musíte zadať dátumy od kedy do kedy');
    }
});

document.getElementById('objednat').addEventListener('click', function () {


    if (startDate != null && endDate != null) {
        let cart = JSON.parse(localStorage.getItem('cart')) || [];
        // Pridáme nový produkt s dátumami do košíka
        cart.push({
            id: product.idProdukt,
            nazov: product.nazov,
            image: product.obrazok,
            startDate: startDateElement.textContent ? startDateElement.textContent : null,
            endDate: endDateElement.textContent ? endDateElement.textContent : null
        });
        // Uložíme aktualizovaný zoznam do localStorage
        updateCartCount();
        resetSelection();
        localStorage.setItem('cart', JSON.stringify(cart));

        sessionStorage.setItem('showToastr', 'true');
        window.location.reload();

    } else {
        toastr.error("Nevybrali ste dátumy, kedy chcete rezevovať techniku")
    }

});


document.getElementById("calendarClose").addEventListener("click", () => {
    resetSelection();
    generateCalendar();
});

document.getElementById("resetButton").addEventListener("click", () => {
    resetSelection(); // Vymaže výber
    updateSelectedDates(); // Aktualizuje zobrazenie vybraných dátumov
});


function vratDatumyObjednavok(idProdukt) {
    const requestData = {
        idProdukt: idProdukt
    }

    axios.post('http://localhost:8080/objednavka/datumy-objednavok', requestData, {
        headers: {
            'Content-Type': 'application/json'
        }
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
        if (item.id == idProdukt) {
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

function updateCartCount() {
    const cart = JSON.parse(localStorage.getItem("cart")) || []; // Načítaj košík z localStorage
    const cartCountElement = document.getElementById("cart-count"); // Element pre počet produktov


    const productCount = cart.length; // Získaj počet produktov v košíku

    if (productCount > 0) {
        cartCountElement.textContent = productCount; // Nastav počet produktov
        cartCountElement.style.display = "inline-block"; // Zobraz počet
    } else {
        cartCountElement.style.display = "none"; // Skry, ak je košík prázdny
    }
};


window.onfocus = function() {
    // Ak je stránka vrátená do popredia, obnov stránku
    window.location.reload();
};

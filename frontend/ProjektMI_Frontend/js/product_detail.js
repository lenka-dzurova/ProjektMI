// Získanie parametrov z URL
const params = new URLSearchParams(window.location.search);
const productId = params.get('id');

// Príklad pomocou Axios na načítanie dát o produkte
axios.get(`http://localhost:8080/produkt/get-produkt/${productId}`, {withCredentials: true})
    .then(response => {
        const product = response.data;
        // Naplnenie HTML elementov detailmi produktu
        document.querySelector('.big-img img').src = `data:image/jpeg;base64,${product.obrazok}`;
        document.querySelector('h2').textContent = product.nazov;
        document.querySelector('h6').textContent = product.popis;
    })
    .catch(error => console.error('Error fetching product details:', error));

const calendarElement = document.getElementById("calendar");
const startDateElement = document.getElementById("start-date").querySelector("span");
const endDateElement = document.getElementById("end-date").querySelector("span");
const monthYearElement = document.getElementById("month-year");
const prevButton = document.getElementById("prev");
const nextButton = document.getElementById("next");
const confirmButton = document.getElementById("confirmButton");
const productModal = new bootstrap.Modal(document.getElementById("productModal"));

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

        // Ak je deň vybraný, nastavíme ho ako selected
        const selectedDate = new Date(currentYear, currentMonth, day);
        if (selectedDays.includes(selectedDate.toDateString())) {
            dayElement.classList.add("selected");
        }

        // Pridá event listener na kliknutie na deň
        dayElement.addEventListener("click", () => toggleSelection(selectedDate, dayElement));

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
    if (!startDate) {
        // Ak nie je vybraný počiatočný dátum, nastavíme ho
        startDate = selectedDate;
        selectedDays.push(dateString);
        element.classList.add("start-date");
    } else if (!endDate) {
        // Ak je vybraný počiatočný dátum, nastavíme koncový dátum
        endDate = selectedDate;
        selectedDays.push(dateString);
        element.classList.add("end-date");

        // Vyberieme všetky dni medzi počiatočným a koncovým dňom
        selectRange(startDate, endDate);
    } else {
        // Resetujeme výber ak sú už vybrané oba dátumy
        resetSelection();
        toggleSelection(selectedDate, element); // Znovu spustíme toggle pre nový počiatočný dátum
    }

    updateSelectedDates();
}


function selectRange(startDate, endDate) {
    // Reset predchádzajúce označenia
    resetSelection();

    // Ak sú počiatočný a koncový dátum zamenené, prehodíme ich
    if (startDate > endDate) {
        [startDate, endDate] = [endDate, startDate];
    }

    let currentDate = new Date(startDate);
    const lastDate = new Date(endDate);

    // Prejdeme dni medzi počiatočným a koncovým dňom, aj cez rôzne mesiace
    while (currentDate <= lastDate) {
        const day = currentDate.getDate();
        const month = currentDate.getMonth();
        const year = currentDate.getFullYear();
        const dayString = currentDate.toDateString();
        selectedDays.push(dayString);

        // Nájdeme príslušný element dňa a označíme ho
        const dayElement = document.querySelector(`.date:nth-child(${day + (new Date(year, month, 1).getDay())})`);
        if (dayElement) {
            dayElement.classList.add("selected");
        }

        currentDate.setDate(currentDate.getDate() + 1); // Posunieme sa na ďalší deň
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
    console.info(selectedDays);
    if (startDate && endDate) {
        productModal.hide();
    } else {
        toastr.error('Musíte zadať dátumy od kedy do kedy');
    }
});

document.getElementById("calendarClose").addEventListener("click", () => {
    resetSelection();
    generateCalendar();
});

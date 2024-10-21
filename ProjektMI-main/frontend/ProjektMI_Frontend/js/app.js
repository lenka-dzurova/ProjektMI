document.addEventListener("DOMContentLoaded", function () {

    document.getElementById("linkRegistration").addEventListener("click", function (event) {
        event.preventDefault(); // Zabráni predvolenému správaniu odkazu
        document.getElementById("login").style.display = "none"; // Skryje prihlasovací div
        document.getElementById("registration").style.display = "block"; // Zobrazí registračný div
    });

    document.getElementById("linkLogin").addEventListener("click", function (event) {
        event.preventDefault(); // Zabráni predvolenému správaniu odkazu
        document.getElementById("registration").style.display = "none"; // Skryje prihlasovací div
        document.getElementById("login").style.display = "block"; // Zobrazí registračný div

        document.getElementById('meno-register').value = "";
        document.getElementById('priezvisko-register').value = "";
        document.getElementById('email-register').value = "";
        document.getElementById('heslo-register').value = "";
        document.getElementById('tel-register').value = "";
        document.getElementById('ulica-register').value = "";
        document.getElementById('psc-register').value = "";
        document.getElementById('mesto-register').value = "";
    });

    document.getElementById("dataConsent").addEventListener("change", function () {
        document.getElementById("registrationButton").disabled = !this.checked;
    });


    document.getElementById("registrationButton").addEventListener("click", function () {

        let meno = document.getElementById('meno-register').value;
        let priezvisko = document.getElementById('priezvisko-register').value;
        let email = document.getElementById('email-register').value;
        let heslo = document.getElementById('heslo-register').value;
        let telCislo = document.getElementById('tel-register').value;
        let ulica = document.getElementById('ulica-register').value;
        let psc = document.getElementById('psc-register').value;
        let mesto = document.getElementById('mesto-register').value;

        const linkResendEmail = document.getElementById('resendEmail');
        const loadingSpinner = document.getElementById('loading-spinner');

        let isValid = true; // Premenná na sledovanie validácie

        // Vymaž predchádzajúce chybové správy
        document.querySelectorAll('.error-message').forEach(span => span.textContent = '');
        document.querySelectorAll('input').forEach(input => input.classList.remove('invalid'));

        // Validácia jednotlivých polí
        if (!meno) {
            document.getElementById('meno-error').textContent = 'Meno je povinné';
            document.getElementById('meno-register').classList.add('invalid');
            isValid = false;
        }

        if (!priezvisko) {
            document.getElementById('priezvisko-error').textContent = 'Priezvisko je povinné';
            document.getElementById('priezvisko-register').classList.add('invalid');
            isValid = false;
        }

        const emailPattern = /^[a-zA-Z0-9._%+-]+@(stud\.)?uniza\.sk$/;
        if (!email /*|| !emailPattern.test(email)*/) {
            document.getElementById('email-error').textContent = 'Zadajte študenský email';
            document.getElementById('email-register').classList.add('invalid');
            isValid = false;
        }

        if (heslo.length < 6) {
            document.getElementById('heslo-error').textContent = 'Heslo musí mať aspoň 6 znakov';
            document.getElementById('heslo-register').classList.add('invalid');
            isValid = false;
        }

        if (!telCislo) {
            document.getElementById('tel-error').textContent = 'Mesto je povinné';
            document.getElementById('tel-register').classList.add('invalid');
            isValid = false;
        }

        if (!ulica) {
            document.getElementById('ulica-error').textContent = 'Ulica je povinná';
            document.getElementById('ulica-register').classList.add('invalid');
            isValid = false;
        }

        const pscPattern = /^[0-9]{5}$/;
        if (!psc || !pscPattern.test(psc)) {
            document.getElementById('psc-error').textContent = 'Zadajte platné PSČ (5 číslic)';
            document.getElementById('psc-register').classList.add('invalid');
            isValid = false;
        }

        if (!mesto) {
            document.getElementById('mesto-error').textContent = 'Mesto je povinné';
            document.getElementById('mesto-register').classList.add('invalid');
            isValid = false;
        }


        // Ak je všetko validné, môžeš odoslať formulár
        if (isValid) {

            const submitButton = document.getElementById('registrationButton');


            // Zobraz načítavací obrázok a deaktivuj tlačidlo
            submitButton.style.display = 'none';
            loadingSpinner.style.display = 'inline-block';
            linkResendEmail.style.display = 'block'


            const register = {
                meno: meno,
                priezvisko: priezvisko,
                email: email,
                heslo: heslo,
                telCislo: telCislo,
                ulica: ulica,
                psc: psc,
                mesto: mesto
            }

            axios.post('http://localhost:8080/register', register, {
                headers: {
                    'Content-Type': 'application/json', // Nastav formát dát ako JSON
                    'Accept': 'application/json'        // Server vráti odpoveď vo formáte JSON
                }
            }).then(response => {
                if (response.status === 201) {
                    loadingSpinner.style.display = 'none';
                    toastr.info('Na email vám prišlo potvrdenie registrácie, prosím potvdte.', 'Potvrdenie registrácie');

                } else if (response.status === 409) {
                    loadingSpinner.style.display = 'none';
                    toastr.error('Tento email už bol zaregistrovaný', 'Existujúci email');
                }
            })
        }

        linkResendEmail.addEventListener("click", function (event) {
            event.preventDefault();
            loadingSpinner.style.display = 'inline-block';
            axios.post('http://localhost:8080/resend-verification-email', {
                email: email
            }).then(response => {
                console.log(response.status);
                if (response.status === 200) {
                    loadingSpinner.style.display = 'none';
                    toastr.info('Na email vám prišlo potvrdenie registrácie, prosím potvdte.', 'Potvrdenie registrácie');
                }
            });
        });


    });
});


document.getElementById("loginButton").addEventListener("click", () => {
    const login = {
        email: document.getElementById('email-login').value,
        heslo: document.getElementById('heslo-login').value
    }


    if (login.email !== "" && login.heslo !== "") {
        axios.post("http://localhost:8080/login", login, {
            withCredentials: true,
            // headers: {"Content-Type": "application/json; charset=utf-8"}
        }).then(response => {
            if (response.status === 200) {
                window.location.href = "products.html";
            }
        }).catch(error => {
            // Kontrola, či error.response existuje
            if (error.response) {
                // Tu môžeš pristupovať k error.response.status
                if (error.response.status === 401) {
                    // Ak príde 401 z backendu:
                    toastr.error('Nesprávne prihlasovacie údaje. Skúste znova.');
                } else {
                    toastr.error(`Chyba ${error.response.status}: ${error.response.data}`, 'Chyba pri prihlásení');
                    console.error("Chyba pri prihlásení:", error.response.status, error.response.data);
                }
            } else {
                // Chyba, ktorá nie je spojená s odpoveďou servera (napr. problém so sieťou)
                console.error("Chyba: ", error.message);
                toastr.error('Nepodarilo sa pripojiť k serveru.', 'Chyba siete');
            }
        });
    } else {
        console.log("Polia nie su vyplnene");
        document.getElementById("registration-form")
    }
});








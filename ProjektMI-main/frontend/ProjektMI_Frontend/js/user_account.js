import {fetchFullUser, fetchUser} from "./helpers.js";


fetchFullUser().then((result) => {
    console.log(result)

    document.getElementById("email").value = result.email;
    document.getElementById("first-name").value = result.meno;//meno
    document.getElementById("last-name").value = result.priezvisko;
    document.getElementById("city").value = result.mesto;
    document.getElementById("street").value = result.ulica;
    document.getElementById("postal-code").value = result.psc;
    document.getElementById("phone").value = result.telCislo;

}).catch(console.error);

document.getElementById('save').addEventListener('click', zmenProfilPouzivatela)

//document.addEventListener("DOMContentLoaded", function () {
    //document.getElementById("potvrditZmenyProfilu").
//});


function zmenProfilPouzivatela(){

    fetchUser().then((result) => {
        let idPouzivatel;
        console.log(result)
        idPouzivatel = result.id;
        alert(idPouzivatel)
        const requestData = {
            idPouzivatel:idPouzivatel,
            email:document.getElementById("email").value,
            meno:document.getElementById("first-name").value,
            priezvisko:document.getElementById("last-name").value,
            mesto:document.getElementById("city").value,
            ulica:document.getElementById("street").value,
            psc:document.getElementById("postal-code").value,
            telCislo:document.getElementById("phone").value
        }


        axios.post('http://localhost:8080/update-pouzivatel', requestData, {withCredentials: true}).then(response => {
            if (response.status === 200) {
                toastr.success("Profil zmeneny");
            }
        }).catch(error => {
            console.error("Chyba pri ukladaní zmien:", error);
            toastr.error("Nastala chyba pri ukladaní zmien. Skúste to znova.");
        });
    }).catch(console.error);



}
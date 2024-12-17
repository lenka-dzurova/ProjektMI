import {fetchUser} from "./helpers.js";
fetchUser().then((result) => {
    console.log(result)
    document.getElementById("email").value = result.email;
    document.getElementById("first-name").value = result.meno;//meno
    document.getElementById("last-name").value = result.priezvisko;//prieyv
    document.getElementById("city").value = result.mesto;//mesto
    document.getElementById("street").value = result.ulica;//ulica
    document.getElementById("postal-code").value = result.psc;//psc
    document.getElementById("phone").value = result.telCislo;//psc

}).catch(console.error);
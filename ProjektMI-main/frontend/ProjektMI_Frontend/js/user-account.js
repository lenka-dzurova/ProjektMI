import {createFooter, createHeader} from "./neutralne.js";
import {fetchUser} from "./helpers.js";

document.addEventListener("DOMContentLoaded", function () {
    document.body.insertBefore(createHeader(), document.body.firstChild);
    document.body.insertBefore(createFooter(), document.body.lastChild);



    fetchUser().then(response => {
        console.log(response)

    });








});

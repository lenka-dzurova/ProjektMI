

export async function fetchUser() {
    try {
        let response = await axios.get("http://localhost:8080/pouzivatel-udaje", {
            withCredentials: true
        });

        if (response.status === 200) {
            return  response.data; // Uložíme údaje do globálnej premennej

        }
    } catch (error) {
        console.error("Error fetching user:", error);
    }
}
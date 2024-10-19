package de.supercode.bookreviewproject.book;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    private static String adminToken;


    @BeforeEach
    public void setUp() {
        // Vor jedem Test die Datenbank aufräumen, um sicherzustellen, dass Tests nicht voneinander abhängen
        bookRepository.deleteAll();

        // Beispielbuch in die Datenbank einfügen
        Book book = new Book(1L, "Integrationstest Buch", "Integrationstest Autor", "Eine Beschreibung", Set.of(Genre.FANTASY));
        bookRepository.save(book);
    }

    @BeforeAll
    public static void registerAndLoginAdmin(MockMvc mockMvc) throws Exception {
        // Admin registrieren
        String adminJson = "{ \"email\": \"admin@mail.com\", \"password\": \"ichbinadmin\", \"role\": \"ADMIN\" }";
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isOk());

        // Credentials für Basic Auth
        String username = "admin@mail.com";
        String password = "ichbinadmin";
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedAuth;

        // Admin anmelden und JWT-Token erhalten
        MvcResult result = mockMvc.perform(post("/api/auth/signin")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Token extrahieren
        String jsonResponse = result.getResponse().getContentAsString();
        adminToken = JsonPath.parse(jsonResponse).read("$.token");
        assertNotNull(adminToken, "Admin Token should not be null");
    }



    @Test
    public void testGetAllBooks_WithBearerToken() throws Exception {
        // Vorab ein Buch erstellen, um sicherzustellen, dass wir etwas zurückbekommen
        Book book = new Book(1L, "Integrationstest Buch", "Integrationstest Autor", "Eine Beschreibung", Set.of(Genre.FANTASY));
        bookRepository.save(book);

        // Anfrage mit Bearer Token senden
        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)); // Sicherstellen, dass ein Buch zurückgegeben wird
    }

    @Test
    public void testCreateBook_WithBearerToken() throws Exception {
        String bookJson = "{ \"title\": \"Neues Buch\", \"author\": \"Neuer Autor\", \"description\": \"Eine neue Beschreibung\", \"genre\": [\"FANTASY\"] }";

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Neues Buch")); // Überprüfen, ob das Buch korrekt erstellt wurde
    }

    @Test
    public void testUpdateBook_WithBearerToken() throws Exception {
        // Ein Buch erstellen, um es später zu aktualisieren
        Book book = new Book(1L, "Altes Buch", "Alter Autor", "Eine alte Beschreibung", Set.of(Genre.FANTASY));
        bookRepository.save(book);

        String updatedBookJson = "{ \"title\": \"Aktualisiertes Buch\", \"author\": \"Neuer Autor\", \"description\": \"Eine aktualisierte Beschreibung\", \"genre\": [\"FANTASY\"] }";

        mockMvc.perform(put("/api/books/1") // Angenommen, die ID des Buches ist 1
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedBookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Aktualisiertes Buch")); // Überprüfen, ob das Buch aktualisiert wurde
    }

    @Test
    public void testDeleteBook_WithBearerToken() throws Exception {
        // Ein Buch erstellen, um es später zu löschen
        Book book = new Book(1L, "Buch zum Löschen", "Autor", "Beschreibung", Set.of(Genre.FANTASY));
        bookRepository.save(book);

        mockMvc.perform(delete("/api/books/1") // Angenommen, die ID des Buches ist 1
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent()); // Überprüfen, ob das Buch erfolgreich gelöscht wurde
    }

    @Test
    public void testGetBooksSortedByMostReviews_WithBearerToken() throws Exception {
        // Anfrage mit Bearer Token senden
        mockMvc.perform(get("/api/books/most-reviews")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetBooksSortedByBestReviews_WithBearerToken() throws Exception {
        // Anfrage mit Bearer Token senden
        mockMvc.perform(get("/api/books/best-reviews")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testFindBooksByAuthor_WithBearerToken() throws Exception {
        // Ein Buch erstellen, um später zu suchen
        Book book = new Book(1L, "Suchbuch", "Autor", "Beschreibung", Set.of(Genre.FANTASY));
        bookRepository.save(book);

        mockMvc.perform(get("/api/books/author?author=Autor") // Nach Autor suchen
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)); // Sicherstellen, dass das Buch zurückgegeben wird
    }

    @Test
    public void testSearchBooksByTitle_WithBearerToken() throws Exception {
        // Ein Buch erstellen, um später zu suchen
        Book book = new Book(1L, "Suchbuch", "Autor", "Beschreibung", Set.of(Genre.FANTASY));
        bookRepository.save(book);

        mockMvc.perform(get("/api/books/search?title=Suchbuch") // Nach Titel suchen
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)); // Sicherstellen, dass das Buch zurückgegeben wird
    }


    /*


    @Test
    public void testGetAllBooks_WithBearerToken() throws Exception {
        // Admin registrieren
        String adminJson = "{ \"email\": \"admin@mail.com\", \"password\": \"ichbinadmin\", \"role\": \"ADMIN\" }";
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isOk());

        // Credentials für Basic Auth
        String username = "admin@mail.com";
        String password = "ichbinadmin";
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8)); // Ändere dies
        String authHeader = "Basic " + encodedAuth;

        // Admin anmelden und JWT-Token erhalten
        MvcResult result = mockMvc.perform(post("/api/auth/signin")
                        .header("Authorization", authHeader) // Setze den Authorization-Header
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Token extrahieren
        String jsonResponse = result.getResponse().getContentAsString();
        adminToken = JsonPath.parse(jsonResponse).read("$.token"); // Hier muss sichergestellt sein, dass token zurückgegeben wird

        // Sicherstellen, dass der Token nicht null ist
        assertNotNull("Admin Token should not be null", adminToken); // Ändere assertNull zu assertNotNull

        // 1. Anfrage mit Bearer Token senden
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

    }


     */

    /*
    @Test
    public void testGetAllBooks_WithBearerToken() throws Exception {
        System.out.println("Admin registered successfully 1, token: " + adminToken);
        // Admin registrieren
        String adminJson = "{ \"email\": \"admin@mail.com\", \"password\": \"ichbinadmin\", \"role\": \"ADMIN\" }";
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isOk());
        System.out.println("Admin registered successfully 2, token: " + adminToken);

        // Admin anmelden und JWT-Token erhalten
        String loginJson = "{ \"email\": \"admin@mail.com\", \"password\": \"ichbinadmin\" }";
        System.out.println("Admin registered successfully 3, token: " + adminToken);
        MvcResult result = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println("Admin registered successfully 4, token: " + adminToken);
        // Token extrahieren
        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("Login Response: " + jsonResponse); // Debug-Ausgabe
        adminToken = JsonPath.parse(jsonResponse).read("$.token");
        System.out.println("Admin registered successfully 5, token: " + adminToken);
        // 1. Token extrahieren und sicherstellen, dass es nicht null ist
        assertNotNull("Admin Token should not be null", adminToken);
        System.out.println("Admin registered successfully 6, token: " + adminToken);
        // 2. Anfrage mit Bearer Token senden
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Integrationstest Buch"));
        System.out.println("Admin registered successfully 7, token: " + adminToken);
    }

     */
}


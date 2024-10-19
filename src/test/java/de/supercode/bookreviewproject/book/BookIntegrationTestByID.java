package de.supercode.bookreviewproject.book;

import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookIntegrationTestByID {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    private static String adminToken;

    @BeforeAll
    public static void init() throws Exception {
        // Hier ist nichts erforderlich, da die Datenbank durch @BeforeEach vorbereitet wird.
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Vor jedem Test die Datenbank aufräumen
        bookRepository.deleteAll();

        // Beispielbuch in die Datenbank einfügen
        Book book = new Book(1L, "Integrationstest Buch", "Integrationstest Autor", "Eine Beschreibung", Set.of(Genre.FANTASY));
        bookRepository.save(book);

        // Admin-Token zurücksetzen und Admin einloggen
        if (adminToken == null) {
            registerAndCreateAdmin();
        } else {
            loginAdmin();
        }
    }

    private void registerAndCreateAdmin() throws Exception {
        // Admin registrieren
        String adminJson = "{ \"email\": \"admin@mail.com\", \"password\": \"ichbinadmin\", \"role\": \"ADMIN\" }";
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isOk());

        // Dann anmelden und Token erhalten
        loginAdmin();
    }

    private void loginAdmin() throws Exception {
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
    public void testGetBookById_WithBearerToken() throws Exception {
        // Beispielbuch in die Datenbank einfügen
        Book book = new Book(null, "Integrationstest Buch", "Integrationstest Autor", "Eine Beschreibung", Set.of(Genre.FANTASY));
        Book savedBook = bookRepository.save(book); // Speichere das Buch und erhalte die generierte ID

        // Anfrage mit Bearer Token senden
        mockMvc.perform(get("/api/books/" + savedBook.getId()) // Verwende die dynamisch generierte ID
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }


    @Test
    public void testGetAllBooks_WithBearerToken() throws Exception {
        // Anfrage mit Bearer Token senden
        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }


    @Test
    public void testCreateBook_WithBearerToken() throws Exception {
        String bookJson = "{ \"title\": \"Neues Buch1\", \"author\": \"Neuer Autor1\", \"description\": \"Eine neue Beschreibung1\", \"genres\": [\"FANTASY\"] }";

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated());
    }



    @Test
    public void testCreateBook_WithBearerToken2() throws Exception {
        String bookJson = "{ \"title\": \"Neues Buch\", \"author\": \"Neuer Autor\", \"description\": \"Eine neue Beschreibung\", \"genres\": [\"FANTASY\"] }";

        // Perform the request and capture the result
        MvcResult result = mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated())  // Assert the status
                .andReturn();

        // Extract the response content as a String
        String jsonResponse = result.getResponse().getContentAsString();

        // Use JsonPath to parse the response
        Integer createdBookIdInt = JsonPath.parse(jsonResponse).read("$.id");
        Long createdBookId = createdBookIdInt.longValue(); // Convert Integer to Long
        String createdBookTitle = JsonPath.parse(jsonResponse).read("$.title");
        String createdBookAuthor = JsonPath.parse(jsonResponse).read("$.author");
        String createdBookDescription = JsonPath.parse(jsonResponse).read("$.description");

        // Extract the genres from the JSON response
        List<String> genreNames = JsonPath.parse(jsonResponse).read("$.genres");
        Set<Genre> createdBookGenres = genreNames.stream()
                .map(Genre::valueOf) // Convert String to Genre enum
                .collect(Collectors.toSet());

        // Assertions to verify the response content
        assertNotNull(createdBookId, "Book ID should not be null");
        assertEquals("Neues Buch", createdBookTitle, "The title of the created book should match");
        assertEquals("Neuer Autor", createdBookAuthor, "The author of the created book should match");
        assertEquals("Eine neue Beschreibung", createdBookDescription, "The description of the created book should match");
        assertTrue(createdBookGenres.contains(Genre.FANTASY), "The created book should have the genre FANTASY");
    }



}




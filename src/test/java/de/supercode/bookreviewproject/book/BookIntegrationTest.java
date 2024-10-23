package de.supercode.bookreviewproject.book;

// Zusammenspiel zwischen Controller, Service, Repository, DB
// realitätnah

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;


@SpringBootTest // lädt den gesamten Spring Application Context (d.h. alle Beans, Konfigurationen, Abhängigkeiten)
@AutoConfigureMockMvc   // ermöglicht Simulation von HTTP-Anfragen
public class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository; // echtes Repository !

    private static String adminToken;

    @BeforeEach
    public void setUp() throws Exception {

        // vor jedem Test DB aufräumen
        bookRepository.deleteAll();

        // Beispielbuch
        Book book = new Book(null, "Integration Test", "Autor", "Beschreibung", Set.of(Genre.ROMANCE));
        bookRepository.save(book);

        if(adminToken == null){
            registerAdmin();
        }else{
            loginAdmin();
        }
    }

    private void registerAdmin() throws Exception{

        String registerJson = "{ \"email\" : \"admin@mail.com\" , \"password\" : \"admin\" , \"role\" : \"ADMIN\" }";

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isOk());

        loginAdmin();
    }

    private void loginAdmin() throws Exception {

        String username = "admin@mail.com";
        String password = "admin";
        String auth = username + ":" + password; // Authentifizierung formatieren

        String encodeAuth = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        MvcResult result = mockMvc.perform(post("/api/auth/signin")
                .header("Authorization", encodeAuth)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Token extrahieren
        String jsonResponse = result.getResponse().getContentAsString();
        adminToken = JsonPath.parse(jsonResponse).read("$.token");
        assertNotNull(adminToken, "Admintoken darf nicht null sein ");
    }

    @Test
    public void getBookById_Successfulyy() throws Exception {
        Book book = bookRepository.findAll().getFirst();

        mockMvc.perform(get("/api/books/" + book.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Test"));
    }


    @Test
    public void testGetBookById_NotFound() throws Exception {

        mockMvc.perform(get("/api/books/" + 99L)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteBook_Successfully() throws Exception {
        Book book = bookRepository.findAll().getFirst();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/" + book.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertEquals(0, bookRepository.count());
    }

    @Test
    public void testDeleteBook_NotFound() throws Exception {
        // Verwende eine nicht existierende Buch-ID
        Long nonExistentBookId = 999L; // Beispiel-ID, die nicht in der Datenbank vorhanden ist

        // Simuliert einen HTTP-DELETE-Request an /books/{id} mit dem Bearer-Token für die Autorisierung
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/" + nonExistentBookId)
                        .header("Authorization", "Bearer " + adminToken)) // Füge den Bearer-Token hinzu
                .andExpect(status().isNotFound()); // Überprüft, dass der HTTP-Status 404 Not Found zurückgegeben wird
    }

    @Test
    public void testCreateBook_ReturnsCreatedBook() throws Exception {

        String requestJson = "{ \"title\" : \"Harry Potter\" ,\"author\": \"JKR\", \"description\": \"Beschreibung\", \"genres\": [\"FANTASY\"] }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Harry Potter"));

        assertEquals(2, bookRepository.count());
    }

    @Test
    public void testUpdateBook() throws Exception {

        Book book = bookRepository.findAll().getFirst();

        String updateRequestJson = "{ \"title\" : \"Harry Potter\" ,\"author\": \"JKR\", \"description\": \"Beschreibung\", \"genres\": [\"FANTASY\"] }";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/books/" + book.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Harry Potter"));
    }

    @Test
    public void testAccessSuperAdminEndpoit_AsAdmin() throws Exception {
        mockMvc.perform(get("/geheim-superadamin")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isForbidden());
    }
}

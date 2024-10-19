package de.supercode.bookreviewproject.book;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import de.supercode.bookreviewproject.review.Review;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Initialisiert die Mock-Objekte
    }

    @Test
    public void testCreateBook() {


        // (1) Setup: Erstellen des BookRequestDto mit Genres
        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.FANTASY);
        genres.add(Genre.ADVENTURE);

        BookRequestDto request = new BookRequestDto(
                "Der Name des Windes",
                "Patrick Rothfuss",
                "Ein episches Fantasy-Abenteuer über den Waisenjungen Kvothe.",
                genres
        );

        Book savedBook = new Book(1L, "Der Name des Windes", "Patrick Rothfuss", "Ein episches Fantasy-Abenteuer über den Waisenjungen Kvothe.", genres);


        // Mock: Wenn "save" aufgerufen wird, gib das gespeicherte Buch zurück
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);



        // (2) Action: Aufruf der Methode
        BookResponseDto result = bookService.createBook(request);



        // (3) Assertion: Überprüfung, ob das Ergebnis korrekt ist
        assertNotNull(result);
        assertEquals("Der Name des Windes", result.title());
        assertEquals("Patrick Rothfuss", result.author());
        assertTrue(result.genres().contains(Genre.FANTASY));
        assertTrue(result.genres().contains(Genre.ADVENTURE));

        // Verifikation: Überprüfen, dass die Methode "save" aufgerufen wurde
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    public void testGetBookById() {

        // Setup: Erstellen eines Buchs
        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.FANTASY);
        genres.add(Genre.ADVENTURE);

        Book existingBook = new Book(1L, "Der Name des Windes", "Patrick Rothfuss", "Ein episches Fantasy-Abenteuer über den Waisenjungen Kvothe.", genres);


        // Mock: Simuliere das Verhalten von findById
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));

        // Action: Aufruf der Methode
        BookResponseDto result = bookService.getBookById(1L);

        // Assertion: Überprüfung, ob das Ergebnis korrekt ist
        assertNotNull(result);
        assertEquals("Der Name des Windes", result.title());
        assertEquals("Patrick Rothfuss", result.author());

        // Verifikation: Überprüfen, dass die Methode findById aufgerufen wurde
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAllBooks() {
        // (1) Setup: Erstellen einer Liste von Büchern
        Set<Genre> genres1 = new HashSet<>();
        genres1.add(Genre.FANTASY);
        Book book1 = new Book(1L, "Der Name des Windes", "Patrick Rothfuss", "Ein episches Fantasy-Abenteuer über den Waisenjungen Kvothe.", genres1);

        Set<Genre> genres2 = new HashSet<>();
        genres2.add(Genre.ADVENTURE);
        Book book2 = new Book(2L, "Die Furcht des Weisen", "Patrick Rothfuss", "Die Fortsetzung der Geschichte von Kvothe.", genres2);

        List<Book> books = Arrays.asList(book1, book2);

        // Mock: Wenn "findAll" aufgerufen wird, gib die Liste von Büchern zurück
        when(bookRepository.findAll()).thenReturn(books);

        // (2) Action: Aufruf der Methode
        List<BookResponseDto> result = bookService.getAllBooks();

        // (3) Assertion: Überprüfung, ob die zurückgegebene Liste korrekt ist
        assertNotNull(result);
        assertEquals(2, result.size());  // Es sollten zwei Bücher zurückgegeben werden

        // Buch 1 überprüfen
        assertEquals("Der Name des Windes", result.get(0).title());
        assertEquals("Patrick Rothfuss", result.get(0).author());
        assertTrue(result.get(0).genres().contains(Genre.FANTASY));

        // Buch 2 überprüfen
        assertEquals("Die Furcht des Weisen", result.get(1).title());
        assertEquals("Patrick Rothfuss", result.get(1).author());
        assertTrue(result.get(1).genres().contains(Genre.ADVENTURE));

        // Verifikation: Überprüfen, dass die Methode "findAll" aufgerufen wurde
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteBookById() {
        // (1) Setup: Simuliere, dass das Buch existiert
        when(bookRepository.existsById(1L)).thenReturn(true); // Buch existiert
        doNothing().when(bookRepository).deleteById(1L); // Simuliere das Löschen des Buches

        // (2) Action: Aufruf der Methode
        bookService.deleteBookById(1L);

        // (3) Verifikation: Überprüfen, dass die Methode "deleteById" aufgerufen wurde
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteBookById_ThrowsExceptionWhenBookNotFound() {
        // (1) Setup: Simuliere, dass das Buch nicht existiert
        when(bookRepository.existsById(1L)).thenReturn(false); // Buch existiert nicht

        // (2) Action & Assertion: Überprüfen, dass die Methode des bookService die Ausnahme wirft
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            bookService.deleteBookById(1L); // Diese Methode sollte die Ausnahme auslösen
        });

        // (3) Überprüfen der Exception-Nachricht
        assertEquals("Book not found with ID: 1", thrown.getMessage());

        // (4) Verifikation: Überprüfen, dass die Methode "existsById" aufgerufen wurde
        verify(bookRepository, times(1)).existsById(1L);
    }

    @Test
    public void testUpdateBookByID() {
        // (1) Setup: Erstellen von Testdaten
        Long bookId = 1L;
        Set<Genre> newGenres = new HashSet<>(Arrays.asList(Genre.FANTASY, Genre.ADVENTURE));

        BookRequestDto bookRequestDto = new BookRequestDto(
                "Neuer Titel",
                "Neuer Autor",
                "Neue Beschreibung",
                newGenres // Hier werden die neuen Genres gesetzt
        );

        Book existingBook = new Book(bookId, "Alter Titel", "Alter Autor", "Alte Beschreibung", new HashSet<>(Arrays.asList(Genre.ADVENTURE)));

        // Mock: Simuliere den Aufruf von findById, um das vorhandene Buch zurückzugeben
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

        // Mock: Simuliere den Aufruf von save, um das aktualisierte Buch zurückzugeben
        when(bookRepository.save(existingBook)).thenReturn(existingBook);

        // (2) Action: Aufruf der Methode
        BookResponseDto result = bookService.updateBookByID(bookId, bookRequestDto);

        // (3) Assertion: Überprüfen, ob das Ergebnis korrekt ist
        assertNotNull(result);
        assertEquals("Neuer Titel", result.title());
        assertEquals("Neuer Autor", result.author());
        assertEquals("Neue Beschreibung", result.description());
        assertTrue(result.genres().contains(Genre.FANTASY)); // Überprüfen, ob das neue Genre hinzugefügt wurde
        assertTrue(result.genres().contains(Genre.ADVENTURE)); // Überprüfen, ob das andere Genre hinzugefügt wurde

        // Verifikation: Überprüfen, dass die Methoden aufgerufen wurden
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    public void testUpdateBookByID_ThrowsExceptionWhenBookNotFound() {
        // (1) Setup: Simuliere, dass das Buch nicht existiert
        Long bookId = 1L;
        BookRequestDto bookRequestDto = new BookRequestDto(
                "Neuer Titel",
                "Neuer Autor",
                "Neue Beschreibung",
                new HashSet<>(Arrays.asList(Genre.FANTASY))
        );

        // Mock: Simuliere, dass findById ein leeres Optional zurückgibt
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // (2) Action & Assertion: Überprüfen, dass die Methode die Ausnahme wirft
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            bookService.updateBookByID(bookId, bookRequestDto);
        });

        // (3) Überprüfen der Exception-Nachricht
        assertEquals("Book not found with id " + bookId, thrown.getMessage());

        // (4) Verifikation: Überprüfen, dass die Methode "findById" aufgerufen wurde
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    public void testGetBooksSortedByMostReviews() {
        // (1) Setup: Erstellen einer Liste von Büchern mit unterschiedlichen Anzahl an Rezensionen
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Buch Eins");
        book1.setAuthor("Autor Eins");
        book1.setDescription("Beschreibung Eins");
        book1.setReviews(Arrays.asList(new Review(), new Review())); // 2 Rezensionen

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Buch Zwei");
        book2.setAuthor("Autor Zwei");
        book2.setDescription("Beschreibung Zwei");
        book2.setReviews(Collections.singletonList(new Review())); // 1 Rezension

        Book book3 = new Book();
        book3.setId(3L);
        book3.setTitle("Buch Drei");
        book3.setAuthor("Autor Drei");
        book3.setDescription("Beschreibung Drei");
        book3.setReviews(new ArrayList<>()); // 0 Rezensionen

        List<Book> books = Arrays.asList(book1, book2, book3);

        // Mock: Simuliere den Aufruf von findAllByOrderByReviewCountDesc
        when(bookRepository.findAllByOrderByReviewCountDesc()).thenReturn(books);

        // (2) Action: Aufruf der Methode
        List<BookResponseDto> result = bookService.getBooksSortedByMostReviews();

        // (3) Assertion: Überprüfen der Ergebnisse
        assertNotNull(result);
        assertEquals(3, result.size()); // Es sollten drei Bücher zurückgegeben werden
        assertEquals("Buch Eins", result.get(0).title()); // Buch mit den meisten Rezensionen
        assertEquals("Buch Zwei", result.get(1).title());
        assertEquals("Buch Drei", result.get(2).title());

        // Verifikation: Überprüfen, dass die Methode aufgerufen wurde
        verify(bookRepository, times(1)).findAllByOrderByReviewCountDesc();
    }

    @Test
    public void testGetBooksSortedByBestReviews() {
        // (1) Setup: Bücher mit verschiedenen durchschnittlichen Bewertungen simulieren

        // Erstelle Beispielbücher
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");

        Book book3 = new Book();
        book3.setId(3L);
        book3.setTitle("Book 3");
        book3.setAuthor("Author 3");

        // Füge Rezensionen (Reviews) zu den Büchern hinzu
        Review review1Book1 = new Review();
        review1Book1.setStars(5);
        review1Book1.setBook(book1);

        Review review2Book1 = new Review();
        review2Book1.setStars(4);
        review2Book1.setBook(book1);

        book1.setReviews(Arrays.asList(review1Book1, review2Book1));  // Bewertungen: 5, 4 -> Durchschnitt 4.5

        Review review1Book2 = new Review();
        review1Book2.setStars(3);
        review1Book2.setBook(book2);

        Review review2Book2 = new Review();
        review2Book2.setStars(3);
        review2Book2.setBook(book2);

        book2.setReviews(Arrays.asList(review1Book2, review2Book2));  // Bewertungen: 3, 3 -> Durchschnitt 3.0

        Review review1Book3 = new Review();
        review1Book3.setStars(1);
        review1Book3.setBook(book3);

        Review review2Book3 = new Review();
        review2Book3.setStars(2);
        review2Book3.setBook(book3);

        book3.setReviews(Arrays.asList(review1Book3, review2Book3));  // Bewertungen: 1, 2 -> Durchschnitt 1.5

        // Mock für das Verhalten des Repositories
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2, book3));

        // (2) Action: Aufruf der Methode
        List<BookResponseDto> result = bookService.getBooksSortedByBestReviews();

        // (3) Assertion: Überprüfen, ob die Bücher nach Bewertung korrekt sortiert sind
        assertNotNull(result);
        assertEquals(3, result.size());

        // Überprüfen der Sortierreihenfolge (Besten Bewertungen zuerst)
        assertEquals("Book 1", result.get(0).title());  // Durchschnitt: 4.5
        assertEquals("Book 2", result.get(1).title());  // Durchschnitt: 3.0
        assertEquals("Book 3", result.get(2).title());  // Durchschnitt: 1.5

        // Verifikation: Überprüfen, dass die Methode "findAll" aufgerufen wurde
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    public void testFindBooksByAuthor_ReturnsBooksWhenFound() {
        // (1) Setup: Simuliere, dass Bücher für den Autor gefunden werden
        String author = "Autor Eins";

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Buch Eins");
        book1.setAuthor("Autor Eins");
        book1.setDescription("Beschreibung Eins");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Buch Zwei");
        book2.setAuthor("Autor Eins");
        book2.setDescription("Beschreibung Zwei");

        List<Book> books = Arrays.asList(book1, book2);

        // Mock: Simuliere den Aufruf von findByAuthorContainingIgnoreCase
        when(bookRepository.findByAuthorContainingIgnoreCase(author)).thenReturn(books);

        // (2) Action: Aufruf der Methode
        List<BookResponseDto> result = bookService.findBooksByAuthor(author);

        // (3) Assertion: Überprüfen, ob die Bücher korrekt zurückgegeben werden
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Buch Eins", result.get(0).title());
        assertEquals("Buch Zwei", result.get(1).title());

        // Verifikation: Überprüfen, dass die Methode "findByAuthorContainingIgnoreCase" aufgerufen wurde
        verify(bookRepository, times(1)).findByAuthorContainingIgnoreCase(author);
    }

    @Test
    public void testFindBooksByAuthor_ThrowsExceptionWhenNoBooksFound() {
        // (1) Setup: Simuliere, dass keine Bücher für den Autor gefunden werden
        String author = "Unbekannter Autor";

        // Mock: Simuliere, dass findByAuthorContainingIgnoreCase eine leere Liste zurückgibt
        when(bookRepository.findByAuthorContainingIgnoreCase(author)).thenReturn(Collections.emptyList());

        // (2) Action & Assertion: Überprüfen, dass die Methode die Ausnahme wirft
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            bookService.findBooksByAuthor(author);
        });

        // (3) Überprüfen der Exception-Nachricht
        assertEquals("No books found for author: " + author, thrown.getMessage());

        // Verifikation: Überprüfen, dass die Methode "findByAuthorContainingIgnoreCase" aufgerufen wurde
        verify(bookRepository, times(1)).findByAuthorContainingIgnoreCase(author);
    }

    @Test
    public void testSearchBooksByTitle_ReturnsBooksWhenFound() {
        // (1) Setup: Simuliere, dass Bücher mit einem Teil des Titels gefunden werden
        String titlePart = "Eins";

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Buch Eins");
        book1.setAuthor("Autor Eins");
        book1.setDescription("Beschreibung Eins");

        List<Book> books = Collections.singletonList(book1);

        // Mock: Simuliere den Aufruf von findByTitleContainingIgnoreCase
        when(bookRepository.findByTitleContainingIgnoreCase(titlePart)).thenReturn(books);

        // (2) Action: Aufruf der Methode
        List<BookResponseDto> result = bookService.searchBooksByTitle(titlePart);

        // (3) Assertion: Überprüfen, ob das Buch korrekt zurückgegeben wird
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Buch Eins", result.get(0).title());

        // Verifikation: Überprüfen, dass die Methode "findByTitleContainingIgnoreCase" aufgerufen wurde
        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase(titlePart);
    }

    @Test
    public void testSearchBooksByTitle_ThrowsExceptionWhenNoBooksFound() {
        // (1) Setup: Simuliere, dass keine Bücher mit dem Titel gefunden werden
        String titlePart = "Unbekannter Titelteil";

        // Mock: Simuliere, dass findByTitleContainingIgnoreCase eine leere Liste zurückgibt
        when(bookRepository.findByTitleContainingIgnoreCase(titlePart)).thenReturn(Collections.emptyList());

        // (2) Action & Assertion: Überprüfen, dass die Methode die Ausnahme wirft
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            bookService.searchBooksByTitle(titlePart);
        });

        // (3) Überprüfen der Exception-Nachricht
        assertEquals("No books found with title containing: " + titlePart, thrown.getMessage());

        // Verifikation: Überprüfen, dass die Methode "findByTitleContainingIgnoreCase" aufgerufen wurde
        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase(titlePart);
    }


}
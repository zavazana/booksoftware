package de.supercode.bookreviewproject.book;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public class BookControllerTest {

    @Mock
    private static BookService bookService;

    @InjectMocks
    private BookController bookController;

    private static BookResponseDto exampleBookResponseDto;
    private static BookRequestDto exampleBookRequestDto;

    @BeforeAll
    public static void setUpBeforeClass() {

        exampleBookResponseDto = new BookResponseDto(
                1L,
                "Beispiel Buch",
                "Beispiel Autor",
                "Eine Beispielbeschreibung",
                Set.of(Genre.FANTASY)
        );

        exampleBookRequestDto = new BookRequestDto(
                "Neues Buch",
                "Neuer Autor",
                "Neue spannende Beschreibung",
                Set.of(Genre.FANTASY)
        );
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetBookById_ReturnsBookWhenFound() {
        // Arrange
        Long bookId = 1L;
        when(bookService.getBookById(bookId)).thenReturn(exampleBookResponseDto);

        // Act
        ResponseEntity<BookResponseDto> response = bookController.getBookById(bookId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(exampleBookResponseDto, response.getBody());
        verify(bookService, times(1)).getBookById(bookId);
    }

    @Test
    public void testGetAllBooks_ReturnsListOfBooks() {
        // Arrange
        List<BookResponseDto> books = List.of(
                exampleBookResponseDto,
                new BookResponseDto(2L, "Zweites Buch", "Zweiter Autor", "Beschreibung für zweites Buch", Set.of(Genre.THRILLER))
        );
        when(bookService.getAllBooks()).thenReturn(books);

        // Act
        ResponseEntity<List<BookResponseDto>> response = bookController.getAllBooks();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(books, response.getBody());
        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    public void testCreateBook_CreatesBookSuccessfully() {
        // Arrange
        BookResponseDto createdBook = new BookResponseDto(
                3L,                        // ID
                exampleBookRequestDto.title(), // Titel
                exampleBookRequestDto.author(), // Autor
                exampleBookRequestDto.description(), // Beschreibung
                exampleBookRequestDto.genres() // Genres
        );
        when(bookService.createBook(exampleBookRequestDto)).thenReturn(createdBook);

        // Act
        ResponseEntity<BookResponseDto> response = bookController.createBook(exampleBookRequestDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdBook, response.getBody());
        verify(bookService, times(1)).createBook(exampleBookRequestDto);
    }

    @Test
    public void testDeleteBook_DeletesBookSuccessfully() {
        // Arrange
        Long bookId = 1L;

        // Act
        ResponseEntity<Void> response = bookController.deleteBook(bookId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookService, times(1)).deleteBookById(bookId);
    }

    @Test
    public void testUpdateBook_UpdatesBookSuccessfully() {
        // Arrange
        Long bookId = 1L;
        BookRequestDto updatedBookRequestDto = new BookRequestDto(
                "Aktualisiertes Buch",        // Titel
                "Aktualisierter Autor",       // Autor
                "Aktualisierte Beschreibung",  // Beschreibung
                Set.of(Genre.FANTASY) // Genres
        );
        BookResponseDto updatedBook = new BookResponseDto(
                bookId,                       // ID
                updatedBookRequestDto.title(),  // Titel
                updatedBookRequestDto.author(), // Autor
                updatedBookRequestDto.description(), // Beschreibung
                updatedBookRequestDto.genres() // Genres
        );
        when(bookService.updateBookByID(bookId, updatedBookRequestDto)).thenReturn(updatedBook);

        // Act
        ResponseEntity<BookResponseDto> response = bookController.updateBook(updatedBookRequestDto, bookId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedBook, response.getBody());
        verify(bookService, times(1)).updateBookByID(bookId, updatedBookRequestDto);
    }

    @Test
    public void testGetBooksSortedByMostReviews_ReturnsSortedBooks() {
        // Arrange
        List<BookResponseDto> sortedBooks = List.of(
                new BookResponseDto(1L, "Buch Eins", "Autor Eins", "Beschreibung Eins", Set.of(Genre.FANTASY)),
                new BookResponseDto(2L, "Buch Zwei", "Autor Zwei", "Beschreibung Zwei", Set.of(Genre.THRILLER))
        );
        when(bookService.getBooksSortedByMostReviews()).thenReturn(sortedBooks);

        // Act
        ResponseEntity<List<BookResponseDto>> response = bookController.getBooksSortedByMostReviews();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sortedBooks, response.getBody());
        verify(bookService, times(1)).getBooksSortedByMostReviews();
    }

    @Test
    public void testGetBooksSortedByBestReviews_ReturnsSortedBooks() {
        // Arrange
        List<BookResponseDto> sortedBooks = List.of(
                new BookResponseDto(1L, "Buch Eins", "Autor Eins", "Beschreibung Eins", Set.of(Genre.THRILLER))
        );
        when(bookService.getBooksSortedByBestReviews()).thenReturn(sortedBooks);

        // Act
        ResponseEntity<List<BookResponseDto>> response = bookController.getBooksSortedByBestReviews();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sortedBooks, response.getBody());
        verify(bookService, times(1)).getBooksSortedByBestReviews();
    }

    @Test
    public void testFindBooksByAuthor_ReturnsBooks() {
        // Arrange
        String author = "Autor Eins";
        List<BookResponseDto> books = List.of(
                new BookResponseDto(1L, "Buch Eins", "Autor Eins", "Beschreibung Eins", Set.of(Genre.FANTASY))
        );
        when(bookService.findBooksByAuthor(author)).thenReturn(books);

        // Act
        ResponseEntity<List<BookResponseDto>> response = bookController.findBooksByAuthor(author);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(books, response.getBody());
        verify(bookService, times(1)).findBooksByAuthor(author);
    }

    @Test
    public void testSearchBooksByTitle_ReturnsBooks() {
        // Arrange
        String titlePart = "Eins";
        List<BookResponseDto> books = List.of(
                new BookResponseDto(1L, "Buch Eins", "Autor Eins", "Beschreibung Eins", Set.of(Genre.FANTASY))
        );
        when(bookService.searchBooksByTitle(titlePart)).thenReturn(books);

        // Act
        ResponseEntity<List<BookResponseDto>> response = bookController.searchBooksByTitle(titlePart);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(books, response.getBody());
        verify(bookService, times(1)).searchBooksByTitle(titlePart);
    }
}


package de.supercode.bookreviewproject.book;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    BookService bookService;

    public BookController(BookService bookService){
        this.bookService = bookService;
    }

    // ALL -> GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {
        BookResponseDto bookDTO = bookService.getBookById(id);
        return ResponseEntity.ok(bookDTO);
    }

    // ALL -> GET all
    @GetMapping()
    public ResponseEntity<List<BookResponseDto>> getAllBooks() {
        List<BookResponseDto> booksDto = bookService.getAllBooks();
        return ResponseEntity.ok(booksDto);
    }

    // ADMIN -> CREATE
    @PostMapping
    public ResponseEntity<BookResponseDto> createBook(@RequestBody BookRequestDto bookRequestDTO) {
        BookResponseDto createdBook = bookService.createBook(bookRequestDTO);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    // ADMIN -> DELETE (Change from POST to DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }

    // ADMIN -> UPDATE (Change from POST to PUT)
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDto> updateBook(@RequestBody BookRequestDto bookRequestDto, @PathVariable Long id) {
        BookResponseDto updatedBook = bookService.updateBookByID(id, bookRequestDto);
        return ResponseEntity.ok(updatedBook);
    }

    // SORTING

    // ALL -> Sort by the number of reviews (descending)
    @GetMapping("/most-reviews")
    public ResponseEntity<List<BookResponseDto>> getBooksSortedByMostReviews() {
        List<BookResponseDto> books = bookService.getBooksSortedByMostReviews();
        return ResponseEntity.ok(books);
    }

    // ALL -> Sort by best reviews (descending)
    @GetMapping("/best-reviews")
    public ResponseEntity<List<BookResponseDto>> getBooksSortedByBestReviews() {
        List<BookResponseDto> books = bookService.getBooksSortedByBestReviews();
        return ResponseEntity.ok(books);
    }

    // SEARCH

    // ALL -> Search books by author
    @GetMapping("/author")
    public ResponseEntity<List<BookResponseDto>> findBooksByAuthor(@RequestParam String author) {
        List<BookResponseDto> books = bookService.findBooksByAuthor(author);
        return ResponseEntity.ok(books);
    }

    // ALL -> Search books by part of the title
    @GetMapping("/search")
    public ResponseEntity<List<BookResponseDto>> searchBooksByTitle(@RequestParam String title) {
        List<BookResponseDto> books = bookService.searchBooksByTitle(title);
        return ResponseEntity.ok(books);
    }
}



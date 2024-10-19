package de.supercode.bookreviewproject.bookExemplar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-exemplars")
public class BookExemplarController {

    private final BookExemplarService bookExemplarService;

    @Autowired
    public BookExemplarController(BookExemplarService bookExemplarService) {
        this.bookExemplarService = bookExemplarService;
    }

    // ADMIN -> Erstellen eines neuen BookExemplars
    @PostMapping
    public ResponseEntity<BookExemplarDto> createBookExemplar(@RequestBody BookExemplarRequestDto requestDto) {
        BookExemplarDto createdExemplar = bookExemplarService.createBookExemplar(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExemplar);
    }

    // ADMIN -> Holen eines BookExemplars per ID
    @GetMapping("/{id}")
    public ResponseEntity<BookExemplarDto> getBookExemplar(@PathVariable Long id) {
        BookExemplarDto bookExemplar = bookExemplarService.getBookExemplar(id);
        return ResponseEntity.ok(bookExemplar);
    }

    // ADMIN -> Holen aller BookExemplars
    @GetMapping
    public ResponseEntity<List<BookExemplarDto>> getAllBookExemplars() {
        List<BookExemplarDto> bookExemplars = bookExemplarService.getAllBookExemplars();
        return ResponseEntity.ok(bookExemplars);
    }

    // ADMIN -> Holen aller Exemplare eines Buches anhand der bookId
    @GetMapping("/by-book/{bookId}")
    public ResponseEntity<List<BookExemplarDto>> getBookExemplarsByBookId(@PathVariable Long bookId) {
        List<BookExemplarDto> bookExemplars = bookExemplarService.getBookExemplarsByBookId(bookId);
        return ResponseEntity.ok(bookExemplars);
    }

    // ADMIN -> Aktualisieren eines BookExemplars
    @PutMapping("/{id}")
    public ResponseEntity<BookExemplarDto> updateBookExemplar(
            @PathVariable Long id,
            @RequestBody BookExemplarRequestDto requestDto) {

        BookExemplarDto updatedExemplar = bookExemplarService.updateBookExemplar(id, requestDto);
        return ResponseEntity.ok(updatedExemplar);
    }

    // ADMIN -> Löschen eines BookExemplars
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookExemplar(@PathVariable Long id) {
        bookExemplarService.deleteBookExemplar(id);
        return ResponseEntity.noContent().build();
    }
}


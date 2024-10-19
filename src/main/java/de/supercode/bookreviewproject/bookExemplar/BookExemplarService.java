package de.supercode.bookreviewproject.bookExemplar;

import de.supercode.bookreviewproject.bookExemplar.BookExemplarDto;
import de.supercode.bookreviewproject.bookExemplar.BookExemplarRequestDto;
import de.supercode.bookreviewproject.book.Book;
import de.supercode.bookreviewproject.bookExemplar.BookExemplar;
import de.supercode.bookreviewproject.bookExemplar.BookExemplarRepository;
import de.supercode.bookreviewproject.book.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookExemplarService {

    private final BookExemplarRepository bookExemplarRepository;
    private final BookRepository bookRepository;

    @Autowired
    public BookExemplarService(BookExemplarRepository bookExemplarRepository, BookRepository bookRepository) {
        this.bookExemplarRepository = bookExemplarRepository;
        this.bookRepository = bookRepository;
    }

    // Erstellen eines neuen BookExemplars
    public BookExemplarDto createBookExemplar(BookExemplarRequestDto requestDto) {
        Book book = bookRepository.findById(requestDto.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + requestDto.bookId()));

        BookExemplar bookExemplar = new BookExemplar();
        bookExemplar.setBook(book);
        bookExemplar.setStatus(requestDto.status());

        bookExemplar = bookExemplarRepository.save(bookExemplar);

        return mapToDto(bookExemplar);
    }

    // Holen eines BookExemplars per ID
    public BookExemplarDto getBookExemplar(Long id) {
        BookExemplar bookExemplar = bookExemplarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book Exemplar not found with id: " + id));

        return mapToDto(bookExemplar);
    }

    // Alle BookExemplars holen
    public List<BookExemplarDto> getAllBookExemplars() {
        return bookExemplarRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Neue Methode: Holen aller Exemplare eines Buches anhand der bookId
    public List<BookExemplarDto> getBookExemplarsByBookId(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        List<BookExemplar> bookExemplars = bookExemplarRepository.findByBook(book);

        return bookExemplars.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Aktualisieren eines BookExemplars
    public BookExemplarDto updateBookExemplar(Long id, BookExemplarRequestDto requestDto) {
        BookExemplar bookExemplar = bookExemplarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book Exemplar not found with id: " + id));

        Book book = bookRepository.findById(requestDto.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + requestDto.bookId()));

        bookExemplar.setBook(book);
        bookExemplar.setStatus(requestDto.status());

        bookExemplar = bookExemplarRepository.save(bookExemplar);

        return mapToDto(bookExemplar);
    }

    // Löschen eines BookExemplars
    public void deleteBookExemplar(Long id) {
        if (!bookExemplarRepository.existsById(id)) {
            throw new EntityNotFoundException("Book Exemplar not found with id: " + id);
        }
        bookExemplarRepository.deleteById(id);
    }

    private BookExemplarDto mapToDto(BookExemplar bookExemplar) {
        return new BookExemplarDto(
                bookExemplar.getId(),
                bookExemplar.getBook().getId(),
                bookExemplar.getStatus()
        );
    }
}

package de.supercode.bookreviewproject.book;

import de.supercode.bookreviewproject.review.Review;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // ADMIN -> CREATE
    public BookResponseDto createBook(BookRequestDto bookRequestDto) {
        Book book = bookRepository.save(BookMapper.mapToEntity(bookRequestDto));
        return BookMapper.mapToDto(book);
    }

    // ALL -> GET BY ID
    public BookResponseDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        return BookMapper.mapToDto(book);
    }

    // ALL -> GET ALL
    public List<BookResponseDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books
                .stream()
                .map(BookMapper::mapToDto)  // verwende Method-Reference
                .collect(Collectors.toList());
    }

    // ADMIN -> DELETE
    public void deleteBookById(long id){
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found with ID: " + id);
        }

        // Wenn das Buch existiert, lösche es
        bookRepository.deleteById(id);

        // ACHTUNG: TO DO
        // Überlegung: wenn wir ein Buch löschen, werden zeitgleich alle BookExemplare gelöscht & alle Rezensionen
        // Ein BookExemplar kann nicht gelöscht werden, wenn ein Bill dazu existiert
    }


    // ADMIN -> UPDATE BOOK
    @Transactional
    public BookResponseDto updateBookByID(Long bookId, BookRequestDto bookRequestDto){
        // Buch aus der Datenbank abrufen
        Book existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id " + bookId));

        // Alle Felder überschreiben (keine Prüfung nötig, da das Frontend alle Felder mitschickt)
        existingBook.setTitle(bookRequestDto.title());
        existingBook.setAuthor(bookRequestDto.author());
        existingBook.setDescription(bookRequestDto.description());
        existingBook.setGenres(bookRequestDto.genres());

        // Buch speichern und aktualisiertes Objekt zurückgeben
        Book updatedBook = bookRepository.save(existingBook);

        return BookMapper.mapToDto(existingBook);
    }

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    // EXTRA FUNKTIONEN

    // ALL -> Gibt Bücher sortiert nach der Anzahl der Rezensionen (absteigend) zurück
    public List<BookResponseDto> getBooksSortedByMostReviews() {
        List<Book> books = bookRepository.findAllByOrderByReviewCountDesc();
        return books.stream()
                .map(BookMapper::mapToDto)
                .collect(Collectors.toList());
    }

    // ALL -> Gibt Bücher sortiert nach den besten Bewertungen (absteigend) zurück
    public List<BookResponseDto> getBooksSortedByBestReviews() {
        List<Book> books = bookRepository.findAll();

        // Sortiere Bücher basierend auf der durchschnittlichen Bewertung
        List<BookResponseDto> sortedBooks = books.stream()
                .sorted(Comparator.comparingDouble(Book::getAverageRating).reversed()) // Direktes Aufrufen der Methode
                .map(BookMapper::mapToDto)  // Mapping der Book-Entitäten zu den DTOs
                .collect(Collectors.toList());

        return sortedBooks;
    }



    // ALL -> Suche Bücher nach Autor
    public List<BookResponseDto> findBooksByAuthor(String author) {
        List<Book> books = bookRepository.findByAuthorContainingIgnoreCase(author);

        if (books.isEmpty()) {
            throw new EntityNotFoundException("No books found for author: " + author);
        }

        return books.stream()
                .map(BookMapper::mapToDto)
                .collect(Collectors.toList());
    }

    // ALL -> Suche Bücher nach einem Teil des Titels
    public List<BookResponseDto> searchBooksByTitle(String titlePart) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(titlePart);

        if (books.isEmpty()) {
            throw new EntityNotFoundException("No books found with title containing: " + titlePart);
        }

        return books.stream()
                .map(BookMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
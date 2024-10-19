package de.supercode.bookreviewproject.bookExemplar;

import de.supercode.bookreviewproject.book.Book;
import de.supercode.bookreviewproject.bookExemplar.BookExemplar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BookExemplarRepository extends JpaRepository<BookExemplar, Long> {
    // Hinzufügen einer Methode zum Suchen von BookExemplars basierend auf dem Buch
    List<BookExemplar> findByBook(Book book);
}

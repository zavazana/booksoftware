package de.supercode.bookreviewproject.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    // Benutzerdefinierte JPQL-Abfrage, um Bücher nach der durchschnittlichen Bewertung absteigend zu sortieren
    @Query("SELECT b FROM Book b LEFT JOIN b.reviews r GROUP BY b.id ORDER BY AVG(r.stars) DESC")
    List<Book> findAllByOrderByAverageRatingDesc();

    // VORTEIL:
    // Sortierung wird direkt in der Datenbank durchgeführt,
    // was effizient ist, wenn große Datenmengen verarbeitet werden

    // Suche nach Büchern, deren Autor einen bestimmten Teilstring enthält (Groß-/Kleinschreibung wird ignoriert)
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // Suche nach Büchern, deren Titel einen bestimmten Teilstring enthält (Groß-/Kleinschreibung wird ignoriert)
    List<Book> findByTitleContainingIgnoreCase(String titlePart);

    // Benutzerdefinierte Abfrage, um Bücher nach der Anzahl der Rezensionen absteigend zu sortieren
    @Query("SELECT b FROM Book b LEFT JOIN b.reviews r GROUP BY b.id ORDER BY COUNT(r.id) DESC")
    List<Book> findAllByOrderByReviewCountDesc();

}
package de.supercode.bookreviewproject.review;

import de.supercode.bookreviewproject.book.Book;
import de.supercode.bookreviewproject.review.Review;
import de.supercode.bookreviewproject.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Methode, um zu prüfen, ob eine Rezension von einem Benutzer für ein Buch existiert
    boolean existsByBookAndUser(Book book, User user);

    // Finden von Rezensionen zu einem bestimmten Buch
    List<Review> findByBook(Book book);

    // Finden von Rezensionen eines bestimmten Benutzers
    List<Review> findByUser(User user);

}

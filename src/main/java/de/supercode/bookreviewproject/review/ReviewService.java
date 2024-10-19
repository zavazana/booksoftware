package de.supercode.bookreviewproject.review;

import de.supercode.bookreviewproject.book.Book;
import de.supercode.bookreviewproject.loan.Loan;
import de.supercode.bookreviewproject.loan.LoanMapper;
import de.supercode.bookreviewproject.security.User;
import de.supercode.bookreviewproject.book.BookRepository;
import de.supercode.bookreviewproject.security.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // Erstellen einer neuen Rezension
    public ReviewResponseDto createReview(ReviewRequestDto reviewRequestDto, Authentication auth) {
        // Überprüfen, ob der Benutzer authentifiziert ist
        if (!auth.isAuthenticated()) {
            throw new SecurityException("User is not authenticated");
        }

        // Den eingeloggten Benutzer abrufen
        String username = auth.getName();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Das Buch abrufen, zu dem die Rezension gehört
        Book book = bookRepository.findById(reviewRequestDto.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + reviewRequestDto.bookId()));

        // Erstellen der Rezension
        Review review = new Review();
        review.setStars(reviewRequestDto.stars());
        review.setContent(reviewRequestDto.content());
        review.setBook(book);
        review.setUser(currentUser);

        return ReviewMapper.mapToDto(reviewRepository.save(review));
    }

    // Holen aller Rezensionen zu einem Buch
    public List<ReviewResponseDto> getReviewsByBookId(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        List<Review> reviews = reviewRepository.findByBook(book);
        return reviews
                .stream()
                .map(ReviewMapper::mapToDto)  // verwende Method-Reference
                .collect(Collectors.toList());
    }

    // Holen aller Rezensionen eines Benutzers
    public List<ReviewResponseDto> getReviewsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<Review> reviews = reviewRepository.findByUser(user);
        return reviews
                .stream()
                .map(ReviewMapper::mapToDto)  // verwende Method-Reference
                .collect(Collectors.toList());
    }
}
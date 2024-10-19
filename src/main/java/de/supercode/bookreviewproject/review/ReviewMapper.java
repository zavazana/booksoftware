package de.supercode.bookreviewproject.review;

import de.supercode.bookreviewproject.book.BookRepository;
import de.supercode.bookreviewproject.security.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

public class ReviewMapper {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;


    // REVIEW -> REVIEW_DTO
    public static ReviewResponseDto mapToDto(Review review) {
        if (review == null) { return null; }
        return new ReviewResponseDto(review.getId(), review.getUser().getId(), review.getBook().getId(), review.getStars(), review.getContent());
    }


    // REVIEW_DTO -> REVIEW
    public Review mapToEntity(ReviewResponseDto reviewDto) {
        if (reviewDto == null) { return null; }

        Review review = new Review();
        review.setId(reviewDto.id());
        review.setStars(reviewDto.stars());
        review.setContent(reviewDto.content());
        review.setBook(bookRepository.findById(reviewDto.bookId()).orElseThrow(() -> new EntityNotFoundException("Book not found")));
        review.setUser(userRepository.findById(reviewDto.userId()).orElseThrow(() -> new EntityNotFoundException("User not found")));

        return review;
    }
}


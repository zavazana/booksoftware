package de.supercode.bookreviewproject.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // USER -> Erstellen einer neuen Rezension
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewRequestDto reviewRequestDto, Authentication auth) {
        ReviewResponseDto review = reviewService.createReview(reviewRequestDto, auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    // ALL -> Holen aller Rezensionen zu einem Buch
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByBookId(@PathVariable Long bookId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    // ADMIN -> Holen aller Rezensionen eines Benutzers
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByUserId(@PathVariable Long userId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }
}

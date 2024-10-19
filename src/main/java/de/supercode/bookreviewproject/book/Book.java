package de.supercode.bookreviewproject.book;

import de.supercode.bookreviewproject.bookExemplar.BookExemplar;
import de.supercode.bookreviewproject.review.Review;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    private String description;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
    // Liste
    // (+) Reihenfolge beibehalten
    // (+) DB Handling: JPA/Hibernate verwendet für @OneToMany-Beziehungen standardmäßig häufig eine List, um die Reihenfolge und das Mapping effizient zu verwalten.

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookExemplar> exemplars = new ArrayList<>();

    @ElementCollection(targetClass = Genre.class)  // Mapping für Enums
    @Enumerated(EnumType.STRING)  // Speichert den Namen des Enums als String in der DB
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "genre")
    private Set<Genre> genres;
    // LIST VS. SET = Duplikate vs. keine Duplikate
    // genreList.add(BookGenre.FANTASY);
    // genreList.add(BookGenre.FANTASY);  // Duplikat möglich
    // Set<BookGenre> genreSet = EnumSet.of(BookGenre.FANTASY, BookGenre.FANTASY);  // Nur einmal "FANTASY"


    // Dynamische Berechnung der averageRating
    public double getAverageRating() {
        if (reviews.isEmpty()) {
            return 0.0;  // Standardwert, wenn keine Bewertungen vorhanden sind
        }

        return reviews.stream()
                .mapToInt(Review::getStars)
                .average()
                .orElse(0.0);  // Durchschnitt der Sterne oder 0.0
    }

    // Parameterloser Konstruktor
    protected Book() {
        // für JPA/Hibernate erforderlich
    }

    // Konstruktor mit Parametern
    public Book(Long id, String title, String author, String description, Set<Genre> genres) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.genres = genres;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public List<BookExemplar> getExemplars() {
        return exemplars;
    }

    public void setExemplars(List<BookExemplar> exemplars) {
        this.exemplars = exemplars;
    }
}



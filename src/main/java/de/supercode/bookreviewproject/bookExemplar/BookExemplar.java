package de.supercode.bookreviewproject.bookExemplar;

import de.supercode.bookreviewproject.book.Book;
import de.supercode.bookreviewproject.loan.Loan;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class BookExemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Viele Instanzen von BookExemplaren gehören zu genau einer Instanz von Buch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    private BookExemplarStatus status; // Status des Buch-Exemplars

    // Ein BookExemplar kann in mehreren Loans erscheinen, daher OneToMany
    @OneToMany(mappedBy = "bookExemplar")
    private List<Loan> loans;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public BookExemplarStatus getStatus() {
        return status;
    }

    public void setStatus(BookExemplarStatus status) {
        this.status = status;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }
}

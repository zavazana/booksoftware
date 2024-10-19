package de.supercode.bookreviewproject.loan;

import de.supercode.bookreviewproject.bookExemplar.BookExemplar;
import de.supercode.bookreviewproject.security.User;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_exemplar_id")
    private BookExemplar bookExemplar;

    private LocalDate loanDate;     // Datum der Ausleihe
    private LocalDate dueDate;      // Geplantes Rückgabedatum
    private LocalDate returnDate;   // Tatsächliches Rückgabedatum, wenn vorhanden

    // Getter, Setter, Constructor

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BookExemplar getBookExemplar() {
        return bookExemplar;
    }

    public void setBookExemplar(BookExemplar bookExemplar) {
        this.bookExemplar = bookExemplar;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}

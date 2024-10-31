package de.supercode.bookreviewproject.bill;

import de.supercode.bookreviewproject.loan.Loan;
import de.supercode.bookreviewproject.security.User;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Loan loan;

    private LocalDate billDate;
    private int overdueDays;
    private double amount;

    @Enumerated(EnumType.STRING)
    private BillStatus status;

    // Getter und Setter

}

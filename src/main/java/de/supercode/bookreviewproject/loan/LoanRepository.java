package de.supercode.bookreviewproject.loan;

import de.supercode.bookreviewproject.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Finden aller Loans eines bestimmten Benutzers anhand seiner Email
    List<Loan> findByUserEmail(String email);

    // Finden aller Loans, die noch nicht zurückgegeben wurden und überfällig sind
    List<Loan> findByReturnDateIsNullAndDueDateBefore(LocalDate dueDate);

    // Finden aller Loans eines bestimmten Benutzers anhand der User-ID
    List<Loan> findByUserId(Long userId);
}
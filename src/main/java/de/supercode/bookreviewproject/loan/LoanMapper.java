package de.supercode.bookreviewproject.loan;

import de.supercode.bookreviewproject.bookExemplar.BookExemplar;
import de.supercode.bookreviewproject.security.User;

import java.time.LocalDate;
import java.util.Optional;

public class LoanMapper {

    // LOANREQUESTDTO -> LOAN
    public static Loan mapToEntity(LoanRequestDto loanRequestDto, User user, BookExemplar bookExemplar) {
        if (loanRequestDto == null || user == null || bookExemplar == null) {
            return null;
        }

        Loan loan = new Loan(); // ID wird automatisch gesetzt
        loan.setUser(user);
        loan.setBookExemplar(bookExemplar);
        loan.setLoanDate(LocalDate.now()); // Setze das aktuelle Datum als Ausleihdatum
        loan.setDueDate(loan.getLoanDate().plusDays(30)); // Beispiel: 30 Tage nach Ausleihe

        return loan;
    }

    // LOAN -> LOANRESPONSEDTO
    public static LoanResponseDto mapToDto(Loan loan) {
        if (loan == null) {
            return null;
        }

        return new LoanResponseDto(
                loan.getId(),
                Optional.ofNullable(loan.getUser()).map(User::getId).orElse(null), // Holen Sie sich die User-ID, falls vorhanden
                Optional.ofNullable(loan.getBookExemplar()).map(BookExemplar::getId).orElse(null), // Holen Sie sich die BookExemplar-ID, falls vorhanden
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate()
        );
    }
}


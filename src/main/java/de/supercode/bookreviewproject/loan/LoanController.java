package de.supercode.bookreviewproject.loan;

import de.supercode.bookreviewproject.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // USER: Erstellen eines neuen Loans (Buch ausleihen)
    @PostMapping
    public ResponseEntity<LoanResponseDto> createLoan(@RequestBody LoanRequestDto loanRequestDto, Authentication auth) {
        LoanResponseDto loan = loanService.createLoan(loanRequestDto, auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    // ADMIN: Holen aller Loans
    @GetMapping
    public ResponseEntity<List<LoanResponseDto>> getAllLoans() {
        List<LoanResponseDto> loans = loanService.getAllLoans();
        return ResponseEntity.ok(loans);
    }

    // ADMIN: Holen aller Loans eines spezifischen Benutzers anhand der User-ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanResponseDto>> getLoansByUserId(@PathVariable Long userId) {
        List<LoanResponseDto> loans = loanService.getLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }

    // USER: Holen aller Loans eines eingeloggten Benutzers (eigene Loans)
    @GetMapping("/me")
    public ResponseEntity<List<LoanResponseDto>> getLoansFromYou(Authentication auth) {
        List<LoanResponseDto> loans = loanService.getLoansFromYou(auth);
        return ResponseEntity.ok(loans);
    }

    // USER: Setzen des Rückgabedatums für ein spezifisches Buch
    @PutMapping("/return/{loanId}")
    public ResponseEntity<LoanResponseDto> setReturnDate(@PathVariable Long loanId, Authentication auth) {
        LoanResponseDto updatedLoan = loanService.setReturnDate(loanId, auth);
        return ResponseEntity.ok(updatedLoan);
    }

    // ADMIN: Holen aller überfälligen und nicht zurückgegebenen Loans
    @GetMapping("/overdue")
    public ResponseEntity<List<LoanResponseDto>> getOverdueLoans() {
        List<LoanResponseDto> overdueLoans = loanService.getOverdueLoans();
        return ResponseEntity.ok(overdueLoans);
    }
}


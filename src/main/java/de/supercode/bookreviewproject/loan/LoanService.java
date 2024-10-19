package de.supercode.bookreviewproject.loan;

import de.supercode.bookreviewproject.book.Book;
import de.supercode.bookreviewproject.book.BookMapper;
import de.supercode.bookreviewproject.bookExemplar.BookExemplar;
import de.supercode.bookreviewproject.bookExemplar.BookExemplarRepository;
import de.supercode.bookreviewproject.bookExemplar.BookExemplarStatus;
import de.supercode.bookreviewproject.security.User;
import de.supercode.bookreviewproject.security.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookExemplarRepository bookExemplarRepository;

    // USER -> CREATE NEW LOAN
    public LoanResponseDto createLoan(LoanRequestDto loanRequestDto, Authentication auth) {

        // Überprüfen, ob der Benutzer authentifiziert ist
        if (!auth.isAuthenticated()) {
            throw new SecurityException("User is not authenticated");
        }

        // Den eingeloggten Benutzer abrufen
        String username = auth.getName();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Überprüfen, ob das Buch existiert
        BookExemplar bookExemplar = bookExemplarRepository.findById(loanRequestDto.bookExemplarId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + loanRequestDto.bookExemplarId()));

        // Überprüfen, ob das Buch AVAILABLE ist
        if (bookExemplar.getStatus() != BookExemplarStatus.AVAILABLE) {
            throw new RuntimeException("BookExemplar is not available.");
        }

        // Loan erstellen & BookExemplar auf loaned setzten
        Loan loan = new Loan();
        loan.setUser(currentUser);
        loan.setBookExemplar(bookExemplar);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusWeeks(2));  // Default 2-week loan period

        bookExemplar.setStatus(BookExemplarStatus.LOANED);

        return LoanMapper.mapToDto(loanRepository.save(loan));

    }

    // ADMIN -> GET ALL LOANS
    public List<LoanResponseDto> getAllLoans() {
        List<Loan> loans = loanRepository.findAll();
        return loans
                .stream()
                .map(LoanMapper::mapToDto)  // verwende Method-Reference
                .collect(Collectors.toList());
    }


    // ADMIN -> GET ALL LOANS BY SPECIFIC USER
    public List<LoanResponseDto> getLoansByUserId(Long userId) {
        List<Loan> loans = loanRepository.findByUserId(userId);
        return loans
                .stream()
                .map(LoanMapper::mapToDto)  // verwende Method-Reference
                .collect(Collectors.toList());
    }


    // USER -> GET ALL LOANS BY YOURSELF
    public List<LoanResponseDto> getLoansFromYou(Authentication auth) {

        // Überprüfen, ob der Benutzer authentifiziert ist
        if (!auth.isAuthenticated()) {
            throw new SecurityException("User is not authenticated");
        }

        // Den eingeloggten Benutzer abrufen
        String username = auth.getName();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<Loan> loans = loanRepository.findByUserId(currentUser.getId());
        return loans
                .stream()
                .map(LoanMapper::mapToDto)  // verwende Method-Reference
                .collect(Collectors.toList());
    }

    // USER -> Set return date for a loan
    public LoanResponseDto setReturnDate(Long loanId, Authentication auth) {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);

        if (loanOpt.isPresent()) {
            Loan loan = loanOpt.get();
            loan.setReturnDate(LocalDate.now());
            Optional<BookExemplar> bookExemplarOpt = bookExemplarRepository.findById(loan.getBookExemplar().getId());
            if(bookExemplarOpt.isPresent()) {
                BookExemplar bookExemplar = bookExemplarOpt.get();
                bookExemplar.setStatus(BookExemplarStatus.AVAILABLE);
            }
            return LoanMapper.mapToDto(loanRepository.save(loan));
        } else {
            throw new RuntimeException("Loan not found.");
        }
    }

    // ADMIN -> Get all loans that are not yet returned and overdue
    public List<LoanResponseDto> getOverdueLoans() {
        List<Loan> loans = loanRepository.findByReturnDateIsNullAndDueDateBefore(LocalDate.now());
        return loans
                .stream()
                .map(LoanMapper::mapToDto)  // verwende Method-Reference
                .collect(Collectors.toList());
    }
}

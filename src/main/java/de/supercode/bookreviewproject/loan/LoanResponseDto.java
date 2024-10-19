package de.supercode.bookreviewproject.loan;

import java.time.LocalDate;

public record LoanResponseDto (
    Long id,
    Long userId,
    Long bookExemplarId,
    LocalDate loanDate,
    LocalDate dueDate,
    LocalDate returnDate
){}

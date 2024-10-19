package de.supercode.bookreviewproject.bookExemplar;

public enum BookExemplarStatus {
    AVAILABLE,    // Das Buch ist verfügbar
    LOANED,       // Das Buch ist ausgeliehen
    RESERVED,     // Das Buch ist reserviert
    LOST,         // Das Buch wurde als verloren gemeldet
    DAMAGED,      // Das Buch ist beschädigt
    OVERDUE       // Das Buch ist überfällig (nicht rechtzeitig zurückgegeben)
}

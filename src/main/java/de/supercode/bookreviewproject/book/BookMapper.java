package de.supercode.bookreviewproject.book;

import java.util.ArrayList;
import java.util.HashSet;

public class BookMapper {

    // BOOKDTO -> BOOK
    public static Book mapToEntity(BookRequestDto bookRequestDto) {
        if (bookRequestDto == null) {
            return null;
        }

        Book book = new Book(); // Book_ID wird automatisch gesetzt
        book.setTitle(bookRequestDto.title());
        book.setAuthor(bookRequestDto.author());
        book.setDescription(bookRequestDto.description());
        book.setGenres(bookRequestDto.genres());

        // Setze die Genres, Duplikate werden automatisch entfernt
        book.setGenres(new HashSet<>(bookRequestDto.genres())); // Optional, falls genres bereits ein Set ist

        // Setze reviews und exemplars auf leere Listen
        book.setReviews(new ArrayList<>());
        book.setExemplars(new ArrayList<>());

        // DENN ACHTUNG:
        // Wenn reviews null ist:
        // int size = book.getReviews().size(); -> NullPointerException
        // VS.
        // Wenn reviews eine leere Liste ist
        // int size = book.getReviews().size(); -> size ist 0, keine Ausnahme

        return book;
    }


    // BOOK -> BOOKDTO
    public static BookResponseDto mapToDto(Book book) {
        if (book == null) {
            return null;
        }

        return new BookResponseDto(book.getId(), book.getTitle(), book.getAuthor(), book.getDescription(), book.getGenres());

    }
}


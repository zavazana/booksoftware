package de.supercode.bookreviewproject.security;

public record AuthDto(
        String email,
        String password,
        String role
) {
}

package de.supercode.bookreviewproject.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
public class AuthentificationService {
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private TokenService tokenService;

    public AuthentificationService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public User signUp(AuthDto dto){
        User user = new User();
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole("ADMIN");
        try {
            User savedUser = userRepository.save(user);
            System.out.println("User saved: " + savedUser);
            return savedUser;
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            throw e; // Hier könnte eine spezifische Fehlerbehandlung hinzugefügt werden
        }
    }

    public String getJwt(Authentication authentication){
        return tokenService.generateToken(authentication);
    }

}

package de.supercode.bookreviewproject.security;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    AuthentificationService authentificationService;

    public AuthController(AuthentificationService authentificationService) {
        this.authentificationService = authentificationService;
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtDto> signin(Authentication authentication){
        return ResponseEntity.ok(new JwtDto(authentificationService.getJwt(authentication)));
    }

    @PostMapping("/signup")
    public User signup(@RequestBody AuthDto dto){
        return authentificationService.signUp(dto);
    }

    @GetMapping("/logout")
    public void logout(HttpSession session){
        session.invalidate();
    }
}

package sellik.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sellik.models.UserLoginModel;
import sellik.models.UserRegistrationModel;
import sellik.services.AuthService;
import sellik.utils.TokenPair;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginModel loginModel) {
        TokenPair tokenPair = authService.loginUser(loginModel);
        ResponseCookie cookieRefreshJwt = ResponseCookie.from("refreshJwt", tokenPair.getJwtRefreshToken())
                .httpOnly(true)
                .secure(true)
                .maxAge(60 * 2)
                .sameSite("Strict")
                .path("/")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieRefreshJwt.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenPair.getJwtToken())
                .body("Successful login");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegistrationModel registrationModel) {
        try {
            return ResponseEntity.ok(authService.registerUser(registrationModel));
        } catch (BadRequestException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}

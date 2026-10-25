package ai.fitnova.api;
import ai.fitnova.api.ApiDtos.*; import ai.fitnova.service.AuthService; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor public class AuthController {private final AuthService auth;
 @PostMapping("/signup") ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(auth.signup(r));}
 @PostMapping("/login") AuthResponse login(@Valid @RequestBody LoginRequest r){return auth.login(r);}}

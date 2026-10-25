package ai.fitnova.service;

import ai.fitnova.api.ApiDtos.*;
import ai.fitnova.domain.Profile;
import ai.fitnova.domain.User;
import ai.fitnova.repo.ProfileRepository;
import ai.fitnova.repo.UserRepository;
import ai.fitnova.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository users; private final ProfileRepository profiles; private final PasswordEncoder encoder; private final JwtService jwt;
    @Transactional public AuthResponse signup(SignupRequest request) {
        String email=request.email().trim().toLowerCase();
        if(users.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("Email already registered");
        User user=new User(); user.setFullName(request.fullName().trim()); user.setEmail(email); user.setPasswordHash(encoder.encode(request.password())); users.save(user);
        Profile profile=new Profile(); profile.setUser(user); profiles.save(profile);
        return response(user, false);
    }
    @Transactional(readOnly=true) public AuthResponse login(LoginRequest request) {
        User user=users.findByEmailIgnoreCase(request.email().trim()).filter(u->encoder.matches(request.password(),u.getPasswordHash()))
            .orElseThrow(()->new BadCredentialsException("Invalid email or password"));
        boolean complete=profiles.findByUserId(user.getId()).map(Profile::isOnboardingComplete).orElse(false);
        return response(user, complete);
    }
    private AuthResponse response(User user, boolean complete){return new AuthResponse(jwt.create(user.getId(),user.getEmail()),new UserView(user.getId(),user.getFullName(),user.getEmail(),complete));}
}

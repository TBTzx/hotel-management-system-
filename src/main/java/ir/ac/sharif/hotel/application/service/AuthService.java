package ir.ac.sharif.hotel.application.service;

import ir.ac.sharif.hotel.domain.exception.NotFoundException;
import ir.ac.sharif.hotel.domain.exception.ValidationException;
import ir.ac.sharif.hotel.domain.model.user.Credentials;
import ir.ac.sharif.hotel.domain.model.user.Role;
import ir.ac.sharif.hotel.domain.model.user.User;
import ir.ac.sharif.hotel.infrastructure.repository.UserRepository;
import ir.ac.sharif.hotel.infrastructure.security.PasswordHasher;
import ir.ac.sharif.hotel.util.Validators;

import java.util.Objects;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
    }

    public User register(Credentials credentials) {
        Validators.requireUsername(credentials.username());
        Validators.requirePassword(credentials.password());

        userRepository.findByUsername(credentials.username()).ifPresent(u -> {
            throw new ValidationException("Username already exists");
        });

        String hash = PasswordHasher.hash(credentials.password());
        User user = new User(credentials.username(), hash, Role.USER);
        userRepository.save(user);
        return user;
    }

    public User login(Credentials credentials) {
        Validators.requireUsername(credentials.username());
        Validators.requirePassword(credentials.password());

        User user = userRepository.findByUsername(credentials.username())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!PasswordHasher.verify(credentials.password(), user.getPasswordHash())) {
            throw new ValidationException("Invalid username or password");
        }
        return user;
    }
}

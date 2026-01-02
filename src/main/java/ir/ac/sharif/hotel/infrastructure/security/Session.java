package ir.ac.sharif.hotel.infrastructure.security;

import ir.ac.sharif.hotel.domain.model.user.User;

import java.util.Optional;


public class Session {
    private User currentUser;

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public Optional<User> currentUser() {
        return Optional.ofNullable(currentUser);
    }
}

package ir.ac.sharif.hotel.application.service;

import ir.ac.sharif.hotel.application.policy.AccessPolicy;
import ir.ac.sharif.hotel.domain.exception.NotFoundException;
import ir.ac.sharif.hotel.domain.model.user.Role;
import ir.ac.sharif.hotel.domain.model.user.User;
import ir.ac.sharif.hotel.infrastructure.repository.UserRepository;

import java.util.List;
import java.util.Objects;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
    }


    public void setRole(User actor, String targetUserId, Role newRole) {
        AccessPolicy.requireRole(actor, Role.ADMIN_MAIN);

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new NotFoundException("Target user not found"));
        target.setRole(newRole);
        userRepository.save(target);
    }

    public List<User> listUsers(User actor) {
        AccessPolicy.requireAny(actor, Role.ADMIN_MAIN, Role.ADMIN_RESERVATION, Role.ADMIN_SERVICES);
        return userRepository.findAll();
    }
}

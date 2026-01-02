package ir.ac.sharif.hotel.app;

import ir.ac.sharif.hotel.domain.model.user.Role;
import ir.ac.sharif.hotel.domain.model.user.User;
import ir.ac.sharif.hotel.infrastructure.repository.UserRepository;
import ir.ac.sharif.hotel.infrastructure.security.PasswordHasher;


public final class Bootstrap {
    private Bootstrap() {}

    public static User ensureMainAdmin(UserRepository userRepo) {
        ensureDefaultAdmins(userRepo);
        return userRepo.findByUsername("root").orElseThrow();
    }

    public static void ensureDefaultAdmins(UserRepository userRepo) {
        ensureAdmin(userRepo, "root", "root1234", Role.ADMIN_MAIN);
        ensureAdmin(userRepo, "admin_res", "admin1234", Role.ADMIN_RESERVATION);
        ensureAdmin(userRepo, "admin_srv", "admin1234", Role.ADMIN_SERVICES);
    }

    private static void ensureAdmin(UserRepository userRepo, String username, String password, Role role) {
        if (userRepo.findByUsername(username).isPresent()) return;
        String hash = PasswordHasher.hash(password);
        userRepo.save(new User(username, hash, role));
    }
}

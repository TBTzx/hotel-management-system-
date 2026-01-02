package ir.ac.sharif.hotel.application.policy;

import ir.ac.sharif.hotel.domain.exception.AccessDeniedException;
import ir.ac.sharif.hotel.domain.model.user.Role;
import ir.ac.sharif.hotel.domain.model.user.User;


public final class AccessPolicy {
    private AccessPolicy() {}

    public static void requireRole(User user, Role required) {
        if (user == null) throw new AccessDeniedException("Not authenticated");
        if (user.getRole() != required) {
            throw new AccessDeniedException("Requires role: " + required + " (current: " + user.getRole() + ")");
        }
    }

    public static void requireAny(User user, Role... allowed) {
        if (user == null) throw new AccessDeniedException("Not authenticated");
        for (Role r : allowed) {
            if (user.getRole() == r) return;
        }
        throw new AccessDeniedException("Access denied for role: " + user.getRole());
    }
}

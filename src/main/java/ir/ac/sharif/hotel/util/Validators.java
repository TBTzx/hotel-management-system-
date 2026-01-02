package ir.ac.sharif.hotel.util;

import ir.ac.sharif.hotel.domain.exception.ValidationException;

public final class Validators {
    private Validators() {}

    public static void requireUsername(String username) {
        if (username == null || username.isBlank())
            throw new ValidationException("Username cannot be empty");
        if (username.length() < 3 || username.length() > 32)
            throw new ValidationException("Username length must be 3..32");
        if (!username.matches("[A-Za-z0-9._-]+"))
            throw new ValidationException("Username contains invalid characters");
    }

    
public static void requirePassword(String password) {
    if (password == null) throw new ValidationException("Password is required");
    String p = password.strip();
    if (p.length() < 8 || p.length() > 64) {
        throw new ValidationException("Password length must be 8..64");
    }
    boolean hasLetter = false, hasDigit = false;
    for (int i = 0; i < p.length(); i++) {
        char c = p.charAt(i);
        if (Character.isLetter(c)) hasLetter = true;
        if (Character.isDigit(c)) hasDigit = true;
    }
    if (!hasLetter || !hasDigit) {
        throw new ValidationException("Password must contain letters and digits");
    }
}
}

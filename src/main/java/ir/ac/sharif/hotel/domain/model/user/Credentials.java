package ir.ac.sharif.hotel.domain.model.user;
import java.io.Serializable;

import java.util.Objects;

public final class Credentials implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String username;
    private final String password;

    public Credentials(String username, String password) {
        this.username = Objects.requireNonNull(username, "username");
        this.password = Objects.requireNonNull(password, "password");
    }

    public String username() { return username; }
    public String password() { return password; }
}

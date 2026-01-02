package ir.ac.sharif.hotel.domain.model.user;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String username;
    private final String passwordHash;
    private Role role;
    private final Instant createdAt;

    public User(String username, String passwordHash, Role role) {
        this.id = UUID.randomUUID().toString();
        this.username = Objects.requireNonNull(username, "username");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.role = Objects.requireNonNull(role, "role");
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public Instant getCreatedAt() { return createdAt; }

    public void setRole(Role role) {
        this.role = Objects.requireNonNull(role, "role");
    }
}

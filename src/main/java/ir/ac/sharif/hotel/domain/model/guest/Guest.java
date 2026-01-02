package ir.ac.sharif.hotel.domain.model.guest;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Guest implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String fullName;
    private final String nationalId;

    public Guest(String fullName, String nationalId) {
        this.id = UUID.randomUUID().toString();
        this.fullName = Objects.requireNonNull(fullName, "fullName");
        this.nationalId = Objects.requireNonNull(nationalId, "nationalId");
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getNationalId() { return nationalId; }
}

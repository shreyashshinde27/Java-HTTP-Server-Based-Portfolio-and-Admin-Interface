import java.sql.Timestamp;

public class ContactMessage {
    private int id;
    private String name;
    private String email;
    private String message;
    private Timestamp createdAt;

    public ContactMessage(int id, String name, String email, String message, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
} 
//they say
package models;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message { private int id;
    private String subject;
    private String content;
    private LocalDateTime timestamp;
    private String sender;
    private String recipient;
    private boolean isRead;

    /**
     * Full constructor with all properties
     */
    public Message(int id, String subject, String content, LocalDateTime timestamp, String recipient) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.timestamp = (timestamp != null) ? timestamp : LocalDateTime.now();
        this.sender = "USER"; // Default sender is current user
        this.recipient = recipient;
        this.isRead = false;
    }


    public Message(String subject, String content, String recipient) {
        this(0, subject, content, LocalDateTime.now(), recipient);
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    /**
     * Format the timestamp for display
     */
    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy - HH:mm");
        return timestamp.format(formatter);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", content='" + (content.length() > 20 ? content.substring(0, 20) + "..." : content) + '\'' +
                ", timestamp=" + getFormattedTimestamp() +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}

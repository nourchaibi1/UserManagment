//they say
package utils;

import models.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageService {
    // Static list to store messages (in-memory storage)
    private static final List<Message> messageQueue = new CopyOnWriteArrayList<>();

    // List of observers (dashboard controllers) that will be notified of new messages
    private static final List<MessageObserver> observers = new ArrayList<>();

    /**
     * Interface for observers that want to be notified of new messages
     */
    public interface MessageObserver {
        void onNewMessage(Message message);
    }

    /**
     * Register a dashboard controller as an observer
     */
    public static void addObserver(MessageObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);

            // Send existing messages to the new observer
            for (Message message : messageQueue) {
                observer.onNewMessage(message);
            }
        }
    }

    /**
     * Remove an observer
     */
    public static void removeObserver(MessageObserver observer) {
        observers.remove(observer);
    }

    /**
     * Send a message and notify all observers
     */
    public boolean sendMessage(Message message) {
        try {
            // Add message to the queue
            messageQueue.add(message);

            // Notify all observers about the new message
            for (MessageObserver observer : observers) {
                observer.onNewMessage(message);
            }

            // In a real application, you would also save to database here
            // But we're keeping the existing code intact

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all messages (for initial loading)
     */
    public List<Message> getAllMessages() {
        return new ArrayList<>(messageQueue);
    }
}

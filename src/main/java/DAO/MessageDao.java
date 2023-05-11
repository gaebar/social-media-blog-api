package DAO;

import Model.Message;
import java.util.List;


public interface MessageDao {
    // Define CRUD (Create, Retrieve, Update, Delete) operations
    Message getMessageById(int messageId); // Method to retrieve a specific messages by its ID.
    List<Message> getAllMessages(); // Method to retrieve all the messages from the database.
    boolean createMessage(Message message); // Method to create a new messages in the database.
    boolean updateMessage(Message message); // Method to update an exiting message in the database.
    boolean deleteMessage(int messageId); // Method to delete a message by its ID.
}

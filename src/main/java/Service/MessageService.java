package Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import DAO.MessageDao;
import Model.Account;
import Model.Message;
import io.javalin.http.NotFoundResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* The Service class contains the business logic for the Message objects and sits sits between the web layer (controller)
    and persistence layer (DAO).
 */

public class MessageService {
    private MessageDao messageDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    // Default constructor initializing the MessageDao object
    public MessageService() {
        messageDao = new MessageDao();
    }

    // Constructor that allows an external MessageDao to be used, useful for testing
    // purposes.
    public MessageService(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    // Retrieve a Message by its ID using the MessageDao
    public Optional<Message> getMessageById(int id) {
        LOGGER.info("Fetching message with ID: " + id);
        try {
            Optional<Message> message = messageDao.get(id);
            if (!message.isPresent()) {
                throw new ServiceException("Message not found");
            }
            LOGGER.info("Fetched message: " + message.orElse(null));
            return message;
        } catch (SQLException e) {
            throw new ServiceException("Error accessing the database", e);
        }
    }

    // Retrieve all messages using the MessageDao
    public List<Message> getAllMessages() {
        LOGGER.info("Fetching all messages");
        try {
            List<Message> messages = messageDao.getAll();
            LOGGER.info("Fetched " + messages.size() + " messages");
            return messages;
        } catch (SQLException e) {
            throw new ServiceException("Error accessing the database", e);
        }
    }

    // Retrieve all messages posted by a specific account
    public List<Message> getMessagesByAccountId(int accountId) {
        LOGGER.info("Fetching messages posted by ID account: " + accountId);
        try {
            List<Message> messages = messageDao.getMessagesByAccountId(accountId);
            LOGGER.info("Fetched " + messages.size() + " messages");
            return messages;
        } catch (SQLException e) {
            throw new ServiceException("Error accessing the database", e);
        }
    }

    // Insert a new message into the database using the MessageDao/
    // Checks account permissions to ensure that only the message author can create
    // messages on their behalf.
    public Message createMessage(Message message, Optional<Account> account) {
        LOGGER.info("Creating message: " + message);

        if (!account.isPresent()) {
            throw new ServiceException("Account must exist when posting a new message");
        }

        if (message.getMessage_text().length() > 254) {
            throw new ServiceException("Message cannot be over 254 characters");
        }

        validateMessage(message);
        checkAccountPermission(account.get(), message.getPosted_by());
        try {
            Message createdMessage = messageDao.insert(message);
            LOGGER.info("Created message: " + createdMessage);
            return createdMessage;
        } catch (SQLException e) {
            throw new ServiceException("Error accessing the database", e);
        }
    }

    // Update an existing message in the database using the MessageDao.
    // Checks account permissions to ensure that only the message author can update
    // their own messages.
    public Message updateMessage(Message message) {
        LOGGER.info("Updating message: " + message.getMessage_id());

        Optional<Message> retrievedMessage = this.getMessageById(message.getMessage_id());
        retrievedMessage.get().setMessage_text(message.getMessage_text());

        validateMessage(retrievedMessage.get());

        try {
            messageDao.update(retrievedMessage.get());
            LOGGER.info("Updated message: " + message);
            return retrievedMessage.get();
        } catch (SQLException e) {
            throw new ServiceException("Error accessing the database", e);
        }
    }

    // Delete an existing message from the database.
    // Check account permissions to ensure that only the message author can delete
    // their own messages.
    public void deleteMessage(Message message) {
        LOGGER.info("Deleting message: " + message);
        if (message == null) {
            throw new ServiceException("Message and account cannot be null");
        }
        // checkAccountPermission(message.getPosted_by());
        try {
            boolean hasDeletedMessage = messageDao.delete(message);
            if (hasDeletedMessage) {
                LOGGER.info("Deleted message " + message);
            } else {
                throw new NotFoundResponse("Message to delete not found", null);
            }
        } catch (SQLException e) {
            throw new ServiceException("Error accessing the database", e);
        }
    }

    private void validateMessage(Message message) {
        LOGGER.info("Validating message: " + message);
        if (message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()) {
            throw new ServiceException("Message text cannot be null or empty");
        }
        if (message.getMessage_text().length() > 254) {
            throw new ServiceException("Message text cannot exceed 255 characters");
        }
    }

    // Check if the account performing the action is the same as the one that posted
    // the message.
    // This is used to mantain user data integrity and security.
    private void checkAccountPermission(Account account, int posted_by) {
        LOGGER.info("Checking account permissions for messages");
        if (account.getAccount_id() != posted_by) {
            throw new ServiceException("Account not authorized to modify this message");
        }
    }
}

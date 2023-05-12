package Service;

import Model.Message;
import DAO.MessageDao;
import Model.Account;

import java.util.List;
import java.util.Optional;


/* The Service class contains the business logic for the Message objects and sits sits between the web layer (controller) 
    and persistence layer (DAO). 
 */

 public class MessageService {
    private MessageDao messageDao;

// Default constructor initializing the MessageDao object
    public MessageService(){
        messageDao = new MessageDao();
    }

    // Constructor that allows an external MessageDao to be used, useful for testing purposes.
    public MessageService(MessageDao messageDao){
       this.messageDao = messageDao;
    }

    //Retrieve a Message by its ID using the MessageDao
    public Optional<Message> getMessageById(int id){
        Optional <Message> message = messageDao.get(id);
        if(!message.isPresent()){
            throw new RuntimeException("Message not found with id: " + id);
        }
        return message;
    }

    // Retrieve all messages using the MessageDao
    public List<Message> getAllMessages(){
        return messageDao.getAll();
    }

    // Retrieve all messages posted by a specific account
    public List<Message>getMessagesByAccountId(int accountId){
        return messageDao.getMessagesByAccountId(accountId);
    }

    // Insert a new message into the database using the MessageDao/
    // Checks account permissions to ensure that only the message author can create messages on their behalf.
    public Message createMessage(Message message, Account account){
        validateMessage(message);
        checkAccountPermission(account, message.getPosted_by());
        return messageDao.insert(message);
    }

    // Update an existing message in the database using the MessageDao.
    //Checks account permissions to ensure that only the message author can update their own messages.
    public void updateMessage(Message message, Account account){
        validateMessage(message);
        checkAccountPermission(account, message.getPosted_by());
        messageDao.update(message);
    }

    // Delete an existing message from the database.
    // Check account permissions to ensure that only the message author can delete their own messages.
    public void deleteMessage(Message message, Account account){
        checkAccountPermission(account, message.getPosted_by());
        messageDao.delete(message);
    }

    private void validateMessage(Message message){
        if(message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()){
            throw new IllegalArgumentException("Message text cannot be null or empty");
        }
        if(message.getMessage_text().length() > 255){
            throw new IllegalArgumentException("Message text cannot exceed 255 characters");
        }
    }

    // Check if the account performing the action is the same as the one that posted the message.
    // This is used to mantain user data integrity and security.
    private void checkAccountPermission(Account account, int posted_by){
        if(account.getAccount_id()!= posted_by){
            throw new SecurityException("Account not authorized to modify this message");
        }
    }
}


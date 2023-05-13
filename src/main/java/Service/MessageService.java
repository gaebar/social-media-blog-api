package Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import DAO.MessageDao;
import Model.Account;
import Model.Message;


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
        try{
            Optional <Message> message = messageDao.get(id);
            if(!message.isPresent()){
                throw new ServiceException("Message not found");
            }
            return message;
        } catch (SQLException e){
            throw new ServiceException("Error accessing the database", e);
        }
    }

    // Retrieve all messages using the MessageDao
    public List<Message> getAllMessages(){
        try{
            return messageDao.getAll();
        } catch (SQLException e){
            throw new ServiceException("Error accessing the database", e);
        }
    }

    // Retrieve all messages posted by a specific account
    public List<Message>getMessagesByAccountId(int accountId){
        try {
        return messageDao.getMessagesByAccountId(accountId);
        } catch (SQLException e){
            throw new ServiceException("Error accessing the database", e);
        }
    }

    // Insert a new message into the database using the MessageDao/
    // Checks account permissions to ensure that only the message author can create messages on their behalf.
    public Message createMessage(Message message, Account account){
        if(message == null || account == null ){
            throw new ServiceException("Message and account cannot be null");
        }
        validateMessage(message);
        checkAccountPermission(account, message.getPosted_by());
        try{
        return messageDao.insert(message);
        } catch (SQLException e){
            throw new ServiceException("Error accessing the database", e);
        } 
    }

    // Update an existing message in the database using the MessageDao.
    //Checks account permissions to ensure that only the message author can update their own messages.
    public Message updateMessage(int id, Message message, Account account){
        if(message == null || account == null ){
            throw new ServiceException("Message and account cannot be null");
        }
        validateMessage(message);
        checkAccountPermission(account, message.getPosted_by());
        message.setMessage_id(id);
        try {
            messageDao.update(message);
            return message;
        } catch (SQLException e){
            throw new ServiceException("Error accessing the database", e);
        }
    }

    // Delete an existing message from the database.
    // Check account permissions to ensure that only the message author can delete their own messages.
    public void deleteMessage(Message message, Account account){
        if(message == null || account == null ){
            throw new ServiceException("Message and account cannot be null");
        }
        checkAccountPermission(account, message.getPosted_by());
        try {
        messageDao.delete(message);
        } catch (SQLException e){
            throw new ServiceException("Error accessing the database", e);
        }
    }

    private void validateMessage(Message message){
        if(message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()){
            throw new ServiceException("Message text cannot be null or empty");
        }
        if(message.getMessage_text().length() > 255){
            throw new ServiceException("Message text cannot exceed 255 characters");
        }
    }

    // Check if the account performing the action is the same as the one that posted the message.
    // This is used to mantain user data integrity and security.
    private void checkAccountPermission(Account account, int posted_by){
        if(account.getAccount_id()!= posted_by){
            throw new ServiceException("Account not authorized to modify this message");
        }
    }
}


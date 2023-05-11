package Service;

import Model.Message;
import DAO.MessageDao;

import java.util.List;
import java.util.Optional;


/* The Service class contains the business logic for the Message objects and sits sits between the web layer (controller) 
    and persistence layer (DAO). 
 */

 public class MessageService {
    private final MessageDao messageDao;

// Default constructor initializing the MessageDao object
    public MessageService(){
        this.messageDao = new MessageDao();
    }

    // Constructor that allows an external MessageDao to be used, useful for testing purposes.
    public MessageService(MessageDao messageDao){
       this.messageDao = messageDao;
    }

    //Retrieves a Message by its ID using the MessageDao
    public Optional<Message> getMessageById(long id){
        return messageDao.get(id);
    }

    // Retrieves all messages using the MessageDao
    public List<Message> getAllMessages(){
        return messageDao.getAll();
    }

    // Insert a new message into the database using the MessageDao
    public Message createMessage(Message message){
        return messageDao.insert(message);
    }

    // Updates an existing message in the database using the MessageDao
    public void updateMessage(Message message){
        messageDao.update(message);
    }

    // Deletes an existing message from the database
    public void deleteMessage(Message message){
        messageDao.delete(message);
    }
}


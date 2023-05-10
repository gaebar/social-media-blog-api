package DAO;

import Model.Message;
import Util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDaoImpl implements MessageDao {
    private final Connection conn = ConnectionUtil.getConnection();

    // Create a new message
    @Override
    public boolean createMessage(Message message){
        // TODO
    }

    // Retrieve all message from the databse
    @Override
    public List<Message> getAllMessages(){
        // TODO
    }

    // Retrieve a specific message by its ID
    public Message getMessageById(int messageId){
        // TODO
    }

    // Update an existing message in the databaee
    @Override
    public boolean updateMessage(Message message){
        // TODO
    }

    // Delete a message by its ID
    @Override
    public boolean deleteMessage(int messageId){
        // TODO
    }
}

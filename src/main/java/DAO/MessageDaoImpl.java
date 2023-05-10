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

    // Retrieve all message from the databse
    @Override
    public List<Message> getAllMessages(){
        // TODO
    }
   
    // Create a new message
    @Override
    public boolean createMessage(Message message){
        String sql = "INSERT INTO message(posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        try(Connection conn = ConnectionUtil.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, message.getPostedBy());
            stmt.setString(2, message.getMessageText());
            stmt.setLong(3, message.getTimePostedEpoch());

            int count = stmt.executeUpdate();
            if(count > 0){
                return true;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
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

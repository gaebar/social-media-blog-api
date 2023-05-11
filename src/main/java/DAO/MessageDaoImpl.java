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


    // Retrieve a specific message by its ID
    @Override
    public Message getMessageById(int messageId){
        Message message = null;
        try (Connection conn = ConnectionUtil.getConnection()){
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, messageId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                message = new Message(rs.getInt("message_id"), rs.getInt("posted_id"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return message;
    }

    // Retrieve all message from the databse
    @Override
    public List<Message> getAllMessages(){
        List<Message> messages = new ArrayList<>();
        try (Connection conn = ConnectionUtil.getConnection()){
            String sql = "SELECT * FROM message";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                messages.add(new Message(rs.getInt("message_id"), rs.getInt("posted_id"), rs.getString("message_text"), rs.getLong("time_posted_epoch")));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return messages;
    }
   
    // Create a new message
    @Override
    public boolean createMessage(Message message){
        String sql = "INSERT INTO message(posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        try(Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());

            int count = ps.executeUpdate();
            if(count > 0){
                return true;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // Update an existing message in the databaee
    @Override
    public boolean updateMessage(Message message){
        try(Connection conn = ConnectionUtil.getConnection() {
            String sql = "UPDATE message SET posted_by = ?, message_text = ?, time_posted_epoch = ? WHERE message_id =?";
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());
            ps.setInt(4, message.getMessage_id());
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Delete a message by its ID
    // WORK ON THIS
    @Override
    public boolean deleteMessage(int messageId){
        try(Connection conn = ConnectionUtil.getConnection()) {
            String sql = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, messageId);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}

package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Model.Message;
import Util.ConnectionUtil;

// This class implements the DAO for the Message table in the SocialMedia.sql database.
// It provides the CRUD (Create, Retrieve, Update, Delete) operartions for messages.

public class MessageDao implements Dao<Message> {

    // Retrieve a specific message by its ID from the database
    @Override
    public Optional<Message> get(int id) throws SQLException {
        Message message = null;
        // The SQL string is outside the try block as it doesn't require closure like
        // Connection, PreparedStatement, or ResultSet.
        String sql = "SELECT * FROM message WHERE message_id = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            // ResultSet is in a separate try block to ensure it gets closed after use,
            // even if an exceprion is thrown during data processing.
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"),
                            rs.getLong("time_posted_epoch"));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while retrievening the message", e);
        }
        // If the message is not found an empty Optional is returned
        return Optional.ofNullable(message);
    }

    // Retrieves all messages from the database
    @Override
    public List<Message> getAll() throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message";
        Connection conn = ConnectionUtil.getConnection();
        try (
                PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                            rs.getString("message_text"), rs.getLong("time_posted_epoch")));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while retrieving all messages", e);
        }
        return messages;
    }

    // Retrieves all messages posted by a specific account from the database
    public List<Message> getMessagesByAccountId(int accountId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE posted_by =?";
        Connection conn = ConnectionUtil.getConnection();
        try (
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                            rs.getString("message_text"), rs.getLong("time_posted_epoch")));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while retriving a message", e);
        }
        return messages;
    }

    // Insert a new message into the database
    @Override
    public Message insert(Message message) throws SQLException {
        String sql = "INSERT INTO message(posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        Connection conn = ConnectionUtil.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());

            ps.executeUpdate();

            // Retrieve the generated keys (auto-generated ID)
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return new Message(generatedId, message.getPosted_by(), message.getMessage_text(),
                            message.getTime_posted_epoch());
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while inserting a message", e);
        }
        return null;
    }

    // Update an existing message in the database
    @Override
    public boolean update(Message message) throws SQLException {
        String sql = "UPDATE message SET posted_by = ?, message_text = ?, time_posted_epoch = ? WHERE message_id =?";
        int rowsUpdated = 0;
        Connection conn = ConnectionUtil.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());
            ps.setInt(4, message.getMessage_id());
            rowsUpdated = ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error while updating the message", e);
        }
        return rowsUpdated > 0;
    }

    // Delete a message from the databse
    @Override
    public boolean delete(Message message) throws SQLException {
        String sql = "DELETE FROM message WHERE message_id = ?";
        int rowsUpdated = 0;
        Connection conn = ConnectionUtil.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, message.getMessage_id());
            rowsUpdated = ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error message while deleting the message", e);
        }
        return rowsUpdated > 0;
    }
}

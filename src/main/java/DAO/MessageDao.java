package DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
// It provides the CRUD (Create, Retrieve, Update, Delete) operations for messages.

public class MessageDao implements Dao<Message> {

    // Create a Logger instance for this class.
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDao.class);

    private static final String TIME_POSTED_EPOCH = "time_posted_epoch";
    private static final String MESSAGE_ID = "message_id";
    private static final String POSTED_BY = "posted_by";
    private static final String MESSAGE_TEXT = "message_text";

    // Helper method to handle SQLException
    private void handleSQLException(SQLException e, String sql, String errorMessage) {
        LOGGER.error("SQLException Details: {}", e.getMessage());
        LOGGER.error("SQL State: {}", e.getSQLState());
        LOGGER.error("Error Code: {}", e.getErrorCode());
        LOGGER.error("SQL: {}", sql);
        throw new DaoException(errorMessage, e);
    }

    // Retrieve a specific message by its ID from the database
    @Override
    public Optional<Message> get(int id) {
        // The SQL string is outside the try block as it doesn't require closure like
        // Connection, PreparedStatement, or ResultSet.
        String sql = "SELECT * FROM message WHERE " + MESSAGE_ID + " = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            // ResultSet is in a separate try block to ensure it gets closed after use,
            // even if an exception is thrown during data processing.
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMessage(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while retrieving the message with id: " + id);
        }
        return Optional.empty();
    }

    // Retrieves all messages from the database
    @Override
    public List<Message> getAll() {
        String sql = "SELECT * FROM message";
        Connection conn = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while retrieving all messages");
        }
        return messages;
    }

    // Retrieves all messages posted by a specific account from the database
    public List<Message> getMessagesByAccountId(int accountId) {
        String sql = "SELECT * FROM message WHERE " + POSTED_BY + " = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                return mapResultSetToList(rs);
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while retrieving a message by account ID: " + accountId);
        }
        return new ArrayList<>();
    }

    // Insert a new message into the database
    @Override
    public Message insert(Message message) {
        String sql = "INSERT INTO message(" + POSTED_BY + ", " + MESSAGE_TEXT + ", " + TIME_POSTED_EPOCH
                + ") VALUES (?, ?, ?)";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                // Check if any keys were generated
                if (generatedKeys.next()) {
                    // Retrieve the generated ID
                    int generatedId = generatedKeys.getInt(1);
                    // Create a new Message object with the generated ID and other attributes
                    return new Message(generatedId, message.getPosted_by(), message.getMessage_text(),
                            message.getTime_posted_epoch());
                } else {
                    throw new DaoException("Failed to insert message, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while inserting a message");
        }
        throw new DaoException("Failed to insert message");
    }

    // Update an existing message in the database
    @Override
    public boolean update(Message message) {
        String sql = "UPDATE message SET " + POSTED_BY + " = ?, " + MESSAGE_TEXT + " = ?, "
                + TIME_POSTED_EPOCH + " = ? WHERE " + MESSAGE_ID + " = ?";
        int rowsUpdated = 0;
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());
            ps.setInt(4, message.getMessage_id());
            rowsUpdated = ps.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while updating the message with id: " + message.getMessage_id());
        }
        return rowsUpdated > 0;
    }

    // Delete a message from the database
    @Override
    public boolean delete(Message message) {
        String sql = "DELETE FROM message WHERE " + MESSAGE_ID + " = ?";
        int rowsUpdated = 0;
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, message.getMessage_id());
            rowsUpdated = ps.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while deleting the message with id: " + message.getMessage_id());
        }
        return rowsUpdated > 0;
    }

    // Helper method to map ResultSet to Message object
    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        int messageId = rs.getInt(MESSAGE_ID);
        int postedBy = rs.getInt(POSTED_BY);
        String messageText = rs.getString(MESSAGE_TEXT);
        long timePostedEpoch = rs.getLong(TIME_POSTED_EPOCH);
        return new Message(messageId, postedBy, messageText, timePostedEpoch);
    }

    // Helper method to map ResultSet to List of Message objects
    private List<Message> mapResultSetToList(ResultSet rs) throws SQLException {
        List<Message> messages = new ArrayList<>();
        while (rs.next()) {
            messages.add(mapResultSetToMessage(rs));
        }
        return messages;
    }
}

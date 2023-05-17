package DAO;

import Model.Account;
import Util.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// Created a DAO classes for each table in the SocialMedia.sql database (Account, Message).
// This class implements the CRUD (Create, Retrieve, Update, Delete) operations for the Account table in the database.
// Each method creates a PreparedStatement object using the try-with-resources, which helps prevent
// resource leaks.

public class AccountDao implements Dao<Account> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDao.class);

    private static final String ACCOUNT_ID = "account_id";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    // Helper method to handle SQLException
    private void handleSQLException(SQLException e, String sql, String errorMessage) {
        LOGGER.error("SQLException Details: {}", e.getMessage());
        LOGGER.error("SQL State: {}", e.getSQLState());
        LOGGER.error("Error Code: {}", e.getErrorCode());
        LOGGER.error("SQL: {}", sql);
        throw new DaoException(errorMessage, e);
    }
    /*
     * Option is used in the get method to handle the possibility that an account
     * may not exists in the database.
     * Instead of returning null, which can cause problems such as
     * NullPointerExceptions, the method return an Optional <Account>.
     *
     * This Allows us to clearly communicate that an account might be absent and
     * forces the calling code to handle this case explicitly.
     */

    /*
     * The try-with-resources statement is used for 'PreparedStatement',
     * and 'ResultSet' objects. This ensures that each resource will be properly
     * closed even if an exception is thrown, thereby helping to prevent resource
     * leaks in the application.
     *
     * The 'Connection' object isn't included in the try-with-resources block.
     * This is because we're using a singleton connection pattern via the
     * ConnectionUtil.getConnection() method.
     * Including the 'Connection' object in the try-with-resources block would
     * automatically close the connection when the block is exited,
     * which would conflict with the singleton pattern (i.e., the same connection
     * might still be needed elsewhere in the application).
     *
     * It's important to note that in our particular setup, we're using an H2
     * in-memory database, which means the database will be cleared when the
     * application ends.
     * So, while we aren't explicitly closing the connection, this isn't a
     * problem for our specific use-case.
     * In a production environment or with different database setups, careful
     * management
     * of database connections would be crucial to prevent resource leaks.
     *
     * The SQL string is outside the try-with-resources block because it doesn't
     * need to be closed like the PreparedStatement or ResultSet.
     */

    // Retrieve an account by its ID from the database
    @Override
    public Optional<Account> getById(int id) {

        String sql = "SELECT * FROM account WHERE " + ACCOUNT_ID + " = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            // ResultSet is in a separate try block to ensure it gets closed after use,
            // even if an exception is thrown during data processing.
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Account(
                            rs.getInt(ACCOUNT_ID),
                            rs.getString(USERNAME),
                            rs.getString(PASSWORD)));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while retrieving the account with id: " + id);
        }
        return Optional.empty();
    }

    // Retrieve all accounts from the database
    @Override
    public List<Account> getAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM account";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Account account = new Account(
                            rs.getInt(ACCOUNT_ID),
                            rs.getString(USERNAME),
                            rs.getString(PASSWORD));
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while retrieving all the accounts");
        }
        return accounts;
    }

    // Retrieve an account by its username from the database
    public Optional<Account> findAccountByUsername(String username) {

        String sql = "SELECT * FROM account WHERE " + USERNAME + " = ?";
        Connection conn = ConnectionUtil.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Account(
                            rs.getInt(ACCOUNT_ID),
                            rs.getString(USERNAME),
                            rs.getString(PASSWORD)));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while finding account with username: " + username);
        }
        return Optional.empty();
    }

    // Validate login credentials by checking if the provided username and password
    // match an account in the database
    public Optional<Account> validateLogin(String username, String password) {
        String sql = "SELECT * FROM account WHERE " + USERNAME + " = ?";
        Connection conn = ConnectionUtil.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account(
                            rs.getInt(ACCOUNT_ID),
                            rs.getString(USERNAME),
                            rs.getString(PASSWORD));

                    // Compare the provided password with the stored password in the Account object
                    if (Objects.equals(password, account.getPassword())) {
                        // Return an Optional containing the authenticated Account
                        return Optional.of(account);
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while validating login for username: " + username);
        }
        return Optional.empty();
    }

    // Check if a username already exists in the database
    public boolean doesUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM account WHERE " + USERNAME + " = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            } catch (SQLException e) {
                handleSQLException(e, sql, "Error while checking if username exists: " + username);
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while establishing connection");
        }
        return false;
    }

    // Insert a new account into the database
    @Override
    public Account insert(Account account) {
        String sql = "INSERT INTO account (" + USERNAME + ", " + PASSWORD + ") VALUES(?, ?)";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.executeUpdate();

            // Retrieve the generated keys (auto-generated ID)
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedAccountId = generatedKeys.getInt(1);
                    return new Account(generatedAccountId, account.getUsername(), account.getPassword());
                } else {
                    throw new DaoException("Creating account failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Creating account failed due to SQL error", e);
        }
    }

    // Updates an existing account in the database
    @Override
    public boolean update(Account account) {
        String sql = "UPDATE account SET " + USERNAME + " = ?, " + PASSWORD + " = ? WHERE " + ACCOUNT_ID + " = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.setInt(3, account.getAccount_id());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                return true;
            } else {
                throw new DaoException("Updating account failed, no such account found.");
            }
        } catch (SQLException e) {
            throw new DaoException("Updating account failed due to SQL error", e);
        }
    }

    // Deletes an account from the database
    @Override
    public boolean delete(Account account) {
        String sql = "DELETE FROM account WHERE " + ACCOUNT_ID + " = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, account.getAccount_id());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DaoException("Deleting account failed due to SQL error", e);
        }
    }
}

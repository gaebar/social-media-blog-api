package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// Create a DAO classes for each table in the SocialMedia.sql database (Account, Message).
// This class implements the CRUD (Create, Retrieve, Update, Delete) operations for the Account table in the database.
// Each method creates a PreparedStatement object using the try-with-resources, which helps prevent
// resource leaks.

public class AccountDao implements Dao<Account> {

    /*
     * Option is used in the get method to handle the possibility that an account
     * may not exists in the database.
     * Instead of returning null, which can cause problems such as
     * NullPointerExceptions, the method return an Optional <Account>.
     *
     * This Allows us to clearly communicate that an account might be absent and
     * forces the calling code to handle
     * this case explicitly.
     */

    // Retrieve an account by its ID from the database
    @Override
    public Optional<Account> get(int id) throws SQLException {
        // The try-with-resources statement is used for 'Connection',
        // 'PreparedStatement', and 'ResultSet' objects.
        // This ensure that each resource will be properly closed even if an exception
        // is thrown,
        // thereby helping to prevent resource leaks in the application.

        // The SQL string is outside the try block as it doesn't require closure like
        // Connection, PreparedStatement, or ResultSet.
        String sql = "SELECT * FROM account WHERE account_id = ?";
        Connection conn = ConnectionUtil.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            // ResultSet is in a separate try block to ensure it gets closed after use,
            // even if an exception is thrown during data processing.
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Account(
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("password")));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while retrieving the account", e);
        }
        return Optional.empty();
    }

    // Retrieve all accounts from the database
    @Override
    public List<Account> getAll() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM account";
        Connection conn = ConnectionUtil.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Account account = new Account(
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("password"));
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while retrieving all the account", e);
        }
        return accounts;
    }

    // Retrieve an account by its username from the database
    public Optional<Account> findAccountByUsername(String username) throws SQLException {

        String sql = "SELECT * FROM account WHERE username = ?";
        Connection conn = ConnectionUtil.getConnection();

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Account(
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("password")));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while finding account", e);
        }
        return Optional.empty();
    }

    // Validate login credentials by checking if the provided username and password
    // match an account in the database
    public Optional<Account> validateLogin(String username, String password) throws SQLException {
        String sql = "SELECT * FROM account WHERE username = ?";
        Connection conn = ConnectionUtil.getConnection();

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account(
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("password"));

                    // Compare the provided password with the stored password in the Account object
                    if (Objects.equals(password, account.getPassword())) {
                        // Return an Optional containing the authenticated Account
                        return Optional.of(account);
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while validating login", e);
        }
        return Optional.empty();
    }

    // Check if a username already exists in the database
    public boolean doesUsernameExist(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Account WHERE username = ?";
        Connection conn = ConnectionUtil.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            } catch (SQLException e) {
                throw new SQLException("Error while checking if username exists", e);
            }
        } catch (SQLException e) {
            throw new SQLException("Error while establishing connection", e);
        }
        return false;
    }

    // Insert a new account into the database
    @Override
    public Account insert(Account account) throws SQLException {
        String sql = "INSERT INTO account (username, password) VALUES(?, ?)";
        Connection conn = ConnectionUtil.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.executeUpdate();

            // Retrieve the generated keys (auto-generated ID)
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generated_account_id = (int) generatedKeys.getInt(1);
                    return new Account(generated_account_id, account.getUsername(), account.getPassword());
                } else {
                    throw new SQLException("Creating account failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while inserting account", e);
        }
    }

    // Updates an existing account in the database
    @Override
    public boolean update(Account account) throws SQLException {
        String sql = "UPDATE account SET username = ?, password = ? WHERE account_id = ?";
        Connection conn = ConnectionUtil.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.setInt(3, account.getAccount_id());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                return true;
            } else {
                throw new SQLException("Updating account failed, no such account found.");
            }
        } catch (SQLException e) {
            throw new SQLException("Error while updating the account", e);
        }
    }

    // Deletes an account from the database
    @Override
    public boolean delete(Account account) throws SQLException {
        String sql = "DELETE FROM account WHERE account_id = ?";
        Connection conn = ConnectionUtil.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, account.getAccount_id());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error while deleting the account", e);
        }
    }
}

package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// Create a DAO classes for each table in the SocialMedia.sql database (Account, Message).
// This class implements the CRUD (Create, Retrieve, Update, Delete) operartions for the Account table in the databse.
// Each method creates a PreparedStatement object using the try-with-resourses, which helps prevent
// resource leaks.

public class AccountDao implements Dao<Account> {

    /*
     * Option is used in the get method to handle the possibility that an account
     * may not exists in the database.
     * Instead of returning null, which can cause problems such as
     * NullPointerExceprions, the methode retun an
     * Optional <Account>.
     *
     * This Allows us to clearly communicate that an account might be absennt and
     * forces the calling code to handle
     * this case explicity.
     */

    // Retrieves an account by its ID
    @Override
    public Optional<Account> get(int id) throws SQLException {
        // The try-with-resources statement is used for 'Conncection',
        // 'PreparedStatement', and 'ResultSet' objects.
        // This ensure that each resourse will be properly closed even if an exception
        // is thrown,
        // thereby helping to prevent resorce leaks in the application.

        // The SQL string is outside the try block as it doesn't require closure like
        // Connection, PreparedStatement, or ResultSet.
        String sql = "SELECT * FROM account WHERE account_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            // ResultSet is in a separate try block to ensure it gets closed after use,
            // even if an exceprion is thrown during data processing.
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Account(
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("password")));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while retrieveing the account", e);
        }
        return Optional.empty();
    }

    // Retrieves all accounts from the database
    @Override
    public List<Account> getAll() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM account";
        try (Connection conn = ConnectionUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
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

    public Optional<Account> findAccountByUsername(String username) throws SQLException {

        String sql = "SELECT * FROM account WHERE username = ?";

        try (Connection conn = ConnectionUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
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

    public Optional<Account> validateLogin(String username, String password) throws SQLException {
        String sql = "SELECT * FROM account WHERE username = ?";

        try (Connection conn = ConnectionUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account(
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("password"));

                    if (Objects.equals(password, account.getPassword())) {
                        return Optional.of(account);
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error while validating login", e);
        }
        return Optional.empty();
    }

    public boolean doesUsernameExist(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Account WHERE username = ?";
        try (Connection conn = ConnectionUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        }
    }

    // Inserts a new account into the databse
    @Override
    public Account insert(Account account) throws SQLException {
        String sql = "INSERT INTO account (username, password) VALUES(?, ?)";
        // Password is already hashed in the service layer
        try (Connection conn = ConnectionUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.executeUpdate();

            // Retrieve the generated keys (auto-generated ID)
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generated_account_id = (int) generatedKeys.getInt(1);
                    // Returning the account with the hashed password
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
        try (Connection conn = ConnectionUtil.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword()); // password is already ashed
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
        try (Connection conn = ConnectionUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, account.getAccount_id());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new SQLException("Error while deleting the account", e);
        }
    }
}

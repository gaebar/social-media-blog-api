package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


// Create a DAO classes for each table in the SocialMedia.sql database (Account, Message).
// This class implements the CRUD (Create, Retrieve, Update, Delete) operartions for the Account table in the databse.
// Each method creates a PreparedStatement object using the try-with-resourses, which helps prevent
// resource leaks.

public class AccountDao implements Dao<Account> {


    /*
        Option is used in the get method to handle the possibility that an account may not exists in the database.
        Instead of returning null, which can cause problems such as NullPointerExceprions, the methode retun an
        Optional <Account>.

        This Allows us to clearly communicate that an account might be absennt and forces the calling code to handle
        this case explicity.
     */

    // Retrieves an account by its ID
    @Override
    public Optional<Account> get(long id){
        // The try-with-resources statement is used for 'Conncection', 'PreparedStatement', and 'ResultSet' objects.
        // This ensure that each resourse will be properly closed even if an exception is thrown,
        // thereby helping to prevent resorce leaks in the application.
        
        // The SQL string is outside the try block as it doesn't require closure like Connection, PreparedStatement, or ResultSet.
        String sql = "SELECT * FROM account WHERE account_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setLong(1, id);
                // ResultSet is in a separate try block to ensure it gets closed after use,
                // even if an exceprion is thrown during data processing. 
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        return Optional.of(new Account (
                            rs.getInt("account_id"), 
                            rs.getString("username"), 
                            rs.getString("password")
                        ));
                    }
                }
        } catch (SQLException e){
            System.out.println("Error message: " + e.getMessage());
        }
        return Optional.empty();
    }

    // Retrieves all accounts from the database
    @Override
    public List<Account> getAll(){
        List<Account> accounts = new ArrayList<>();  
        String sql = "SELECT * FROM account";
        try(Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    Account account = new Account(
                        rs.getInt("account_id"), 
                        rs.getString("username"), 
                        rs.getString("password")
                    );
                    accounts.add(account);
                
            }
        }
        } catch (SQLException e){
            System.out.println("Error message: " + e.getMessage());
        }
        return accounts;
    }


    // Inserts a new account into the databse
    @Override
    public Account insert(Account account){
        String sql = "INSERT INTO account (username, password) VALUES(?, ?)";
        try(Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                ps.setString(1, account.getUsername());
                ps.setString(2, account.getPassword());
                ps.executeUpdate();

                // Retrieve the generated keys (auto-generated ID)
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if(generatedKeys.next()){
                    int generated_account_id = (int)generatedKeys.getInt(1);
                    return new Account(generated_account_id, account.getUsername(), account.getPassword());
                }
        } catch (SQLException e){
            System.out.println("Error message: " + e.getMessage());
        }
        return null;
    }

    // Updates an existing account in the database
    @Override
    public void update(Account account){
        String sql = "UPDATE account SET username = ?, password = ? WHERE account_id = ?";
        try(Connection conn = ConnectionUtil.getConnection()){ 
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.setInt(3, account.getAccount_id());
            ps.executeUpdate();
        } catch (SQLException e){
            System.out.println("Error message: " + e.getMessage());
        }
    }

    // Deletes an account from the database
    @Override
    public void delete(Account account){
        String sql = "DELETE FROM account WHERE account_id = ?";
        try(Connection conn = ConnectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setInt(1, account.getAccount_id());
                ps.executeUpdate();
        } catch (SQLException e){
            System.out.println("Error message: " + e.getMessage());
        }
    }
}


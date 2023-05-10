package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;


// Create a DAO classes for each table in the SocialMedia.sql database.
// Implement the CRUD (Create, Retrieve, Update, Delete) operartions for the Account table.
// In each method create a PreparedStatement object

public class AccountDaoImpl implements AccountDao {


    @Override
    public List<Account> getAllAccounts(){
        List<Account> accounts = new ArrayList<>();

        try (Connection conn = ConnectionUtil.getConnection()){
            String sql = "SELECT * FROM account";
            try (PreparedStatement stmt = conn.prepareStatement(sql)){
                try (ResultSet rs = stmt.executeQuery()){
                    while (rs.next()){
                        Account account = new Account(
                            rs.getInt("account_id"), 
                            rs.getString("username"), 
                            rs.getString("password"));
                        accounts.add(account);
                    }
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return accounts;
    }

    // Get account by id
    @Override
    public Account getAccountById(int accountId){
        // TODO
    }

    // Create Account
    @Override
    public boolean createAccount(Account account){
        // TODO
    }

    // Update account
    @Override
    public boolean updateAccount(Account account){
        try (Connection conn = ConnectionUtil.getConnection()){
            String sql = "UPDATE account SET username=?, password=? WHERE account_id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, account.getUsername());
                stmt.setString(2, account.getPassword());
                stmt.setInt(3, account.getAccount_id());

                int rowsAffected = stmt.executeUpdate();
               
                return rowsAffected >0;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // Delete account
    @Override
    public boolean deleteAccount(int accountId){
        // TODO
    }

}


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


    // Get account by id
    @Override
    public Account getAccountById(int id){
        try (Connection conn = ConnectionUtil.getConnection()){
            
            String sql = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return new Account (
                    rs.getInt("account_id"), 
                    rs.getString("username"), 
                    rs.getString("password")
                );
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Account> getAllAccounts(){
        List<Account> accounts = new ArrayList<>();

        try (Connection conn = ConnectionUtil.getConnection()){
            String sql = "SELECT * FROM account";
            try (PreparedStatement ps = conn.prepareStatement(sql)){
                try (ResultSet rs = ps.executeQuery()){
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

    // Insert Account
    @Override
    public boolean insertAccount(Account account){
        try (Connection conn = ConnectionUtil.getConnection()){
            
            String sql = "INSERT INTO account (usernam, password) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());

            
            return ps.executeQuery() !=0;


        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // Update account
    @Override
    public boolean updateAccount(Account account){
        try (Connection conn = ConnectionUtil.getConnection()){
            String sql = "UPDATE account SET username = ?, password = ? WHERE account_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, account.getUsername());
                ps.setString(2, account.getPassword());
                ps.setInt(3, account.getAccount_id());

                int rowsAffected = ps.executeUpdate();
               
                return rowsAffected >0;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // Delete account
    @Override
    public boolean deleteAccount(int id){
        try (Connection conn = ConnectionUtil.getConnection()){
            String sql = "DELETE FROM account WHERE account_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);

            return ps.executeUpdate() !=0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;

    }

}


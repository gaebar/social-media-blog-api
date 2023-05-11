package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


// Create a DAO classes for each table in the SocialMedia.sql database.
// Implement the CRUD (Create, Retrieve, Update, Delete) operartions for the Account table.
// In each method create a PreparedStatement object

public class AccountDaoImpl implements Dao<Account> {


    // Get account by id
    @Override
    public Optional<Account> get(long id){
        try (Connection conn = ConnectionUtil.getConnection()){
            String sql = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                return Optional.of(new Account (
                    rs.getInt("account_id"), 
                    rs.getString("username"), 
                    rs.getString("password")
                ));
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Account> getAll(){
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
    public void insert(Account account){
        try (Connection conn = ConnectionUtil.getConnection()){
            
            String sql = "INSERT INTO account (username, password) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Update account
    @Override
    public void update(Account account, String[] params){
        try (Connection conn = ConnectionUtil.getConnection()){
            String sql = "UPDATE account SET username = ?, password = ? WHERE account_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, account.getUsername());
                ps.setString(2, account.getPassword());
                ps.setInt(3, account.getAccount_id());
                ps.executeUpdate();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

    }

    // Delete account
    @Override
    public void delete(Account account){
        try (Connection conn = ConnectionUtil.getConnection()){
            String sql = "DELETE FROM account WHERE account_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, account.getAccount_id());
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}


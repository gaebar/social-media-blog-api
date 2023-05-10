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

    // Create a new account
    @Override
    public Account getAccountById(int id){
        try (Connection conn = ConnectionUtil.getConnection()){
            String sql = "SELECT * FROM account WHERE account_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, id);

                try(ResultSet rs = stmt.executeQuery()){
                
                    if (rs.next()){
                        return new Account(
                            rs.getInt("account_id"), 
                            rs.getString("username"), 
                            rs.getString("password"));
                    }
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    // Get account by id

    // Update account

    // Delete account

}


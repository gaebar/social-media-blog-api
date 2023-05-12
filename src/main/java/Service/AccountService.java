package Service;

import Model.Account;
import DAO.AccountDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import Util.ConnectionUtil;

/* The purpose of a Service class is to contain "business logic" that sits between the web layer (controller) 
    and persistence layer (DAO). 

    The AccountService class provides CRUD operations for the Account model by interacting with the AccountDao class.
    Also, it has two constructors: a default constructor that initializes a new AccountDao object,
    and another that accepts an existing AccountDao object.

 */

public class AccountService {
    private AccountDao accountDao;

// Default constructor initializing the AccountDao object
    public AccountService(){
        accountDao = new AccountDao();
    }

    // Constructor that allows an external AccountDao to be used, useful for testing purposes.
    public AccountService(AccountDao accountDao){
       this.accountDao = accountDao;
    }

    //Retrieves an Account by its ID using the AccountDao
    public Optional<Account> getAccountById(int id){
        return accountDao.get(id);
    }

    // Retrieves all accounts using the AccountDao
    public List<Account> getAllAccounts(){
        return accountDao.getAll();
    }

    public Account register(Account account){
        if(account == null || account.getUsername() == null || account.getUsername().trim().isEmpty() || account.getPassword() == null || account.getPassword().length() < 4){
            throw new IllegalArgumentException("Invalid account information");
        }
        
        String sql = "SELECT * FROM account WHERE username = ?";

        try(Connection connection = ConnectionUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString(1, account.getUsername());
                ResultSet rs = ps.executeQuery();

                if(rs.next()){ // if result set is not empty, account with same username already exists
                    throw new IllegalArgumentException("Username already exists");
                }

                sql = "INSERT INTO account(username, password) VALUES (?, ?)";
                PreparedStatement psInsert = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // use the conncetion to create PreparedStatement
                psInsert.setString(1, account.getUsername());
                psInsert.setString(2, account.getPassword());
                psInsert.executeUpdate();

                try(ResultSet generatedKeys = psInsert.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        account.setAccount_id(generatedKeys.getInt(1));
                    }
                    else {
                        throw new SQLException("Creating account failde, no ID obtained.");
                    }
                }

                return account; // return account after successful registration
            } catch (SQLException e){
                System.out.println("Error message: " + e.getMessage());
                e.printStackTrace();
            }

            return null; // return null if registration failed

    }

    public Account login(Account account){
        if(account == null || account.getUsername() == null || account.getPassword() == null){
            throw new IllegalArgumentException("Account information cannot be null");
        }

        String sql = "SELECT * FROM account WHERE username = ? AND password = ?";

        try(Connection connection = ConnectionUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){
                ps.setString(1, account.getUsername());
                ps.setString(2, account.getPassword());
                ResultSet rs = ps.executeQuery();

                if(rs.next()){ // if result set is not empty, login is successful
                    return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password")); // 
                }else {
                    throw new IllegalArgumentException("Invalid username or password");
                }

            } catch (SQLException e){
                System.out.println("Error message: " + e.getMessage());
                e.printStackTrace();
            }

            return null; // return null if login failed
    }



    // Insert a new account into the database using the AccaountDao
    public Account createAccount(Account account){
        return accountDao.insert(account);
    }

    // Updates an existing account in the database using the AccountDao
    public void updateAccount(Account account){
        accountDao.update(account);
    }

    // Deletes an existing account from the database
    public void deleteAccount(Account account){
        accountDao.delete(account);
    }
}

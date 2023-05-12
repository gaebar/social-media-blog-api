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
        try {
            return accountDao.get(id);
        } catch (SQLException e){
            e.printStackTrace();
            return Optional.empty(); // return an empty Optional if an excpion occurs
        }
    }

    // Retrieves all accounts using the AccountDao
    public List<Account> getAllAccounts(){
        try {
        return accountDao.getAll();
        } catch (SQLException e){
            e.printStackTrace();
            return List.of(); // return an empty list on failure
        }
    }

    // Finds an account by username using the AccountDao
    public Optional<Account> findAccountByUsername(String username){
        try{
            return accountDao.findAccountByUsername(username);
        } catch (SQLException e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // Validate login using the AccountDao
    public Optional<Account> validateLogin(String username, String password){
        try{
            Optional<Account> account = accountDao.validateLogin(username, password);
            return account;
        } catch (SQLException e){
            e.printStackTrace();
            return Optional.empty();
        }
    }


    // Insert a new account into the database using the AccaountDao
    public Account createAccount(Account account){
        try {
            if(account.getUsername().trim().isEmpty() || account.getPassword().length() < 4 || accountDao.doesUsernameExist((account.getUsername()))){
                throw new IllegalArgumentException("Username can not be blank, password must be at least 4 characters long, and the username must be unique.");
            }
            return accountDao.insert(account);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    // Updates an existing account in the database using the AccountDao
    public boolean updateAccount(Account account){
        try{
        return accountDao.update(account);
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    // Deletes an existing account from the database
    public boolean deleteAccount(Account account){
        if(account.getAccount_id() == 0){
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        try {
            return accountDao.delete(account);
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}

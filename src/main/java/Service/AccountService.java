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

    // Finds an account by username using the AccountDao
    public Optional<Account> findAccountByUsername(String username){
        return accountDao.findAccountByUsername(username);
    }

    // Validate login using the AccountDao
    public Optional<Account> validateLogin(String username, String password){
        return accountDao.validateLogin(username, password);
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

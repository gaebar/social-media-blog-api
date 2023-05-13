package Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DAO.AccountDao;
import Model.Account;


/* The purpose of a Service class is to contain "business logic" that sits between the web layer (controller) 
    and persistence layer (DAO). 

    The AccountService class provides CRUD operations for the Account model by interacting with the AccountDao class.
    Also, it has two constructors: a default constructor that initializes a new AccountDao object,
    and another that accepts an existing AccountDao object.

 */

public class AccountService {
    private AccountDao accountDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

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
            LOGGER.error("Exception occured while fetching account", e);
            throw new ServiceException("Exception occured while fetching account", e);
        }
    }

    // Retrieves all accounts using the AccountDao
    public List<Account> getAllAccounts(){
        try {
        return accountDao.getAll();
        } catch (SQLException e){
            LOGGER.error("Exception occured while fetching all accounts", e);
            throw new ServiceException("Exception occured while fetching accounts", e);
        }
    }

    // Finds an account by username using the AccountDao
    public Optional<Account> findAccountByUsername(String username){
        try{
            return accountDao.findAccountByUsername(username);
        } catch (SQLException e){
            LOGGER.error("Exception occured while finding account by username " + username, e);
            throw new ServiceException("Exception occured while finding account by username " + username, e);
        }
    }

    // Validate login using the AccountDao
    public Optional<Account> validateLogin(String username, String password){
        try{
            Optional<Account> account = accountDao.validateLogin(username, password);
            return account;
        } catch (SQLException e){
            LOGGER.error("Exception occured while validating login by username " + username, e);
            throw new ServiceException("Exception occured while validating login by username " + username, e);
        }
    }


    // Insert a new account into the database using the AccaountDao
    public Account createAccount(Account account){
        try {
            validateAccount(account);
            // Hash the password using BCcrypt. This ensure that we never store the actual password on the database.
            String hashedPassword = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt());
            account.setPassword(hashedPassword);
            return accountDao.insert(account);
        } catch (SQLException e){
            LOGGER.error("Exception occured while creating account", e);
            throw new ServiceException("Exception occured while creating account", e);
        }
    }

    // Updates an existing account in the database using the AccountDao
    public boolean updateAccount(Account account){
        try{
        String hashedPassword = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt());
        account.setPassword(hashedPassword);
        return accountDao.update(account);
        } catch (SQLException e){
            LOGGER.error("Exception occured while updating account", e);
            throw new ServiceException("Exception occured while while updating account", e);
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
            LOGGER.error("Exception occured while deleting account", e);
            throw new ServiceException("Exception occured while while deleting account", e);
        }
    }

    private void validateAccount(Account account){
        try{
            if(account.getUsername().trim().isEmpty() || account.getPassword().length() < 4 || accountDao.doesUsernameExist((account.getUsername()))){
                throw new IllegalArgumentException("Username can not be blank, password must be at least 4 characters long, and the username must be unique.");
                }
            } catch (SQLException e){
                LOGGER.error("Exception occured while validating account", e);
                throw new ServiceException("Exception occured while validating account", e);
        }
    }

    // Check if the user exist in the database base on their id
    public boolean accountExists(int accountId){
        try{
            Optional<Account> account = accountDao.get(accountId);
            return account.isPresent();
        } catch (SQLException e){
                LOGGER.error("Exception occured while checking account existence", e);
                throw new ServiceException("Exception occured while checking account existence", e);
        }
    }
}

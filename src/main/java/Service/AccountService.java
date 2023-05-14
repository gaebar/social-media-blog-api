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

    In this class, the logger object LOGGER is used to log different type of messages associated with the operations like
    fetching, updating, creating, and deleting accounts. 

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
        LOGGER.info("Fetching account with ID: " + id);
        try {
            Optional <Account> account = accountDao.get(id);
            LOGGER.info("Fetched account: " + account.orElse(null));
            return account;
        } catch (SQLException e){
            LOGGER.error("Exception occured while fetching account", e);
            throw new ServiceException("Exception occured while fetching account", e);
        }
    }

    // Retrieves all accounts using the AccountDao
    public List<Account> getAllAccounts(){
        LOGGER.info("Fetching all accounts");
        try {
            List<Account> accounts = accountDao.getAll();
            LOGGER.info("Fetched " + accounts.size() + " accounts");
            return accounts;
        } catch (SQLException e){
            LOGGER.error("Exception occured while fetching all accounts", e);
            throw new ServiceException("Exception occured while fetching accounts", e);
        }
    }

    // Finds an account by username using the AccountDao
    public Optional<Account> findAccountByUsername(String username){
        LOGGER.info("Finding account by username: " + username);
        try{
            Optional<Account> account = accountDao.findAccountByUsername(username);
            LOGGER.info("Found account: " + account.orElse(null));
            return account;
        } catch (SQLException e){
            LOGGER.error("Exception occured while finding account by username " + username, e);
            throw new ServiceException("Exception occured while finding account by username " + username, e);
        }
    }

    // Validate login using the AccountDao
    public Optional<Account> validateLogin(String username, String password){
        LOGGER.info("Validating login for username: " + username);
        try{
            Optional<Account> account = accountDao.validateLogin(username, password);
            LOGGER.info("Login validation result: " + account.isPresent());
            return account;
        } catch (SQLException e){
            LOGGER.error("Exception occured while validating login by username " + username, e);
            throw new ServiceException("Exception occured while validating login by username " + username, e);
        }
    }


    // Insert a new account into the database using the AccaountDao
    public Account createAccount(Account account){
        LOGGER.info("Creating account: " + account);
        try {
            validateAccount(account);
            // Hash the password using BCcrypt. This ensure that we never store the actual password on the database.
            String hashedPassword = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt());
            account.setPassword(hashedPassword);
            Account createdAccount = accountDao.insert(account);
            LOGGER.info("Created account: " + createdAccount);
            return createdAccount;
        } catch (SQLException e){
            LOGGER.error("Exception occured while creating account", e);
            throw new ServiceException("Exception occured while creating account", e);
        }
    }

    // Updates an existing account in the database using the AccountDao
    public boolean updateAccount(Account account){
        LOGGER.info("Updating account: " + account);
        try{
        String hashedPassword = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt());
        account.setPassword(hashedPassword);
        boolean updated = accountDao.update(account);
        LOGGER.info("Updated account: " + account + ". Update successful " + updated);
        return updated;
        } catch (SQLException e){
            LOGGER.error("Exception occured while updating account", e);
            throw new ServiceException("Exception occured while while updating account", e);
        }
    }

    // Deletes an existing account from the database
    public boolean deleteAccount(Account account){
        LOGGER.info("Deleting account: " + account);
        if(account.getAccount_id() == 0){
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        try {
            boolean deleted = accountDao.delete(account);
            LOGGER.info("Deleted account: " + account + ". Deletion successful " + deleted);
            return deleted;
        } catch (SQLException e){
            LOGGER.error("Exception occured while deleting account", e);
            throw new ServiceException("Exception occured while while deleting account", e);
        }
    }

    private void validateAccount(Account account){
        LOGGER.info("Validating account: " + account);
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
        LOGGER.info("Cheching account exhitence with ID: " + accountId);
        try{
            Optional<Account> account = accountDao.get(accountId);
            boolean exists = account.isPresent();
            LOGGER.info("Account existence: " + exists);
            return exists;
        } catch (SQLException e){
                LOGGER.error("Exception occured while checking account existence", e);
                throw new ServiceException("Exception occured while checking account existence", e);
        }
    }
}

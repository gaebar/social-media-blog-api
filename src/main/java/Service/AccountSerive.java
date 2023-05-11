package Service;

import Model.Account;
import DAO.AccountDao;

import java.util.List;

/* The purpose of a Service class is to contain "business logic" that sits between the web layer (controller) 
    and persistence layer (DAO). 
 */

public class AccountSerive {
    private AccountDao accountDao;

    public AccountDao(){
        accountDao = new AccountDao();
    }  

    public AccountSerive(AccountDao accountDao){
        this.accountDao = accountDao;
    }

    public List<Account> getAll(){
        return accountDao.getAll();
    }

    // TODO ...
}

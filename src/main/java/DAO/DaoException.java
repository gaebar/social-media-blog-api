package DAO;

// This is a custom exception class that is thrown when an error occurs in the
// DAO
public class DaoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}

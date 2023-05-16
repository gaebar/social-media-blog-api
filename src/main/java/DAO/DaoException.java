package DAO;

// The DaoException class is a custom exception that is thrown when an error occurs in the DAO layer.
// By creating this custom exception, we can encapsulate and handle specific errors that may occur
// in the DAO layer separately from other types of exceptions.

// Extending RuntimeException allows DaoException to be an unchecked exception, meaning it
// does not need to be declared in method signatures or explicitly caught in code.
public class DaoException extends RuntimeException {

    // The serialVersionUID is a version identifier for the Serializable class. It
    // is used during the deserialization process to verify that the sender and
    // receiver of a serialized object have loaded classes for that object that are
    // compatible with respect to serialization. If the serialVersionUID values of
    // the sender and receiver classes are different, deserialization will result in
    // an InvalidClassException.
    private static final long serialVersionUID = 1L;

    // Constructs a new DaoException with the specified error message.
    // @param message the error message
    public DaoException(String message) {
        super(message);
    }

    // Constructs a new DaoException with the specified error message and cause.
    // @param message the error message
    // @param cause the cause of the exception
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}

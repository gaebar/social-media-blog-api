package Service;

// ServiceException is a custom exception used within the service layer.
// It is used to wrap and re-throw exceptions that allows for more specific error handling
// and message passing.

// The ServiceException class extends RuntimeException, which is not typically serialized.
// Additionally, unlike the DaoException class, the ServiceException class does not introduce
// any new fields or change the serialization behavior of its superclass.
// Therefore, adding a serialVersionUID is not necessary in this case.
public class ServiceException extends RuntimeException {

    // Constructor that takes in a custom message for the exception
    // @param message the error message
    public ServiceException(String message) {
        super(message);
    }

    // Constructor that takes in a custom message and the original exception that
    // causes this exception
    // @param message the error message
    // @param cause the cause of the exception
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

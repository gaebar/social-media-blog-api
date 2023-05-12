package Service;

/*
    ServiceException is a custom exception used within the service layer.
    It is used to wrap and re-throw exceptions that allows for more specific error handling
    and message passing,
 */

public class ServiceException extends RuntimeException{

    // Constructor that takes in a custom message for the exception
    public ServiceException(String message){
    super(message);
    }

    // Constructor that takes in a custom message and the original exception that causes this exception
    public ServiceException(String message, Throwable cause){
        super(message, cause);
    }
}

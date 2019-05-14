
package retrospector.hsqldb.exceptions;

public class DatabaseConnectionFailedException extends RuntimeException {

    private static String message = "Database Connection Failed";
    
    public DatabaseConnectionFailedException() {
        super(message);
    }
    
    public DatabaseConnectionFailedException(Throwable throwable) {
        super(message, throwable);
    }

}

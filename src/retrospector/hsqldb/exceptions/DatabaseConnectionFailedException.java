
package retrospector.hsqldb.exceptions;

public class DatabaseConnectionFailedException extends RuntimeException {

    public DatabaseConnectionFailedException() {
        super("Database Connection Failed");
    }

}

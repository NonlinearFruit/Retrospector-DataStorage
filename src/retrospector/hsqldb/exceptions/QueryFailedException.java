
package retrospector.hsqldb.exceptions;

public class QueryFailedException extends RuntimeException{
    public QueryFailedException() {
        super("Query Failed");
    }
    
    public QueryFailedException(String message) {
        super(message);
    }
    
    public QueryFailedException(Throwable ex) {
        super(ex);
    }
}

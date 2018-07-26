
package retrospector.hsqldb.exceptions;

public class TableCreationQueryFailedException extends QueryFailedException {

    public TableCreationQueryFailedException() {
        super("Table Creation Query Failed");
    }
    
    public TableCreationQueryFailedException(Throwable ex) {
        super(ex);
    }

}

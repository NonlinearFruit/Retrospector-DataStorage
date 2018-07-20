
package retrospector.hsqldb.exceptions;

public class TableCreationQueryFailedException extends RuntimeException {

    public TableCreationQueryFailedException() {
        super("Table Creation Query Failed");
    }

}

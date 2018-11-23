
package retrospector.hsqldb.exceptions;

public class ForeignEntityNotFoundException extends RuntimeException{

    public ForeignEntityNotFoundException() {
        super("Foreign Entity Not Found");
    }
}

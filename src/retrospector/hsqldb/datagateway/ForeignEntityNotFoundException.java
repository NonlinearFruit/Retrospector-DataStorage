
package retrospector.hsqldb.datagateway;

class ForeignEntityNotFoundException extends RuntimeException{

    public ForeignEntityNotFoundException() {
        super("Foreign Entity Not Found");
    }
}

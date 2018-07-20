
package retrospector.hsqldb.datagateway;

import retrospector.hsqldb.exceptions.DatabaseConnectionFailedException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnector {
    private String connectionString;
    private Connection connection;
    
    public DbConnector(String connectionString) {
        this.connectionString = connectionString;
        setConnection();
    }
    
    public void endConnection(){
        try {
            DriverManager.getConnection(connectionString+";shutdown=true","SA","");
            connection.close();
            System.out.println("HSQLDB shut down normally");
        } catch (SQLException ex) {
            System.err.println("HSQLDB did not shut down normally");
            System.err.println(ex.getMessage());
        }
    }
    
    private void setConnection() {
        try {
            this.connection = DriverManager.getConnection(connectionString,"SA","");   
        } catch(SQLException ex) {
            throw new DatabaseConnectionFailedException();
        }
    }
    
    public Connection getConnection(){
        return connection;
    }
}


package retrospector.hsqldb.datagateway;

import retrospector.hsqldb.exceptions.DatabaseConnectionFailedException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import retrospector.hsqldb.exceptions.QueryFailedException;

public class DbConnector {
    private String connectionString;
    private Connection connection;
    private QueryRunner queryRunner;
    
    public DbConnector(String connectionString) {
        this.connectionString = connectionString;
        this.queryRunner = new QueryRunner();
        setConnection();
    }
    
    public void exit(){
        try {
            execute("SHUTDOWN");
            DbUtils.close(connection);
            System.out.println("HSQLDB shut down normally");
        } catch (SQLException|QueryFailedException ex) {
            System.err.println("HSQLDB did not shut down normally");
            System.err.println(ex.getMessage());
        }
    }
    
    public <T> T insert(ResultSetHandler<T> handler, String query, Object... parameters) {
        try {
            return queryRunner.insert(getConnection(), query, handler, parameters);
        } catch (SQLException ex) {
            throw new QueryFailedException(ex);
        }
    }
    
    public <T> T select(ResultSetHandler<T> handler, String query, Object... parameters) {
        try {
            return queryRunner.query(getConnection(), query, handler, parameters);
        } catch (SQLException ex) {
            throw new QueryFailedException(ex);
        }
    }
    
    public void execute(String sql, Object... parameters) {
        try {
            queryRunner.update(getConnection(), sql, parameters);
        } catch (SQLException ex) {
            throw new QueryFailedException(ex);
        }
    }
    
    private void setConnection() {
        try {
            this.connection = DriverManager.getConnection(connectionString,"SA","");   
        } catch(SQLException ex) {
            throw new DatabaseConnectionFailedException();
        }
    }
    
    private Connection getConnection(){
        return connection;
    }
}

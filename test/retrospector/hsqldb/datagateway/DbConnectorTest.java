/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package retrospector.hsqldb.datagateway;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import retrospector.hsqldb.exceptions.DatabaseConnectionFailedException;

/**
 *
 * @author root
 */
public class DbConnectorTest {
    
    public static final String testConnectionString = "jdbc:hsqldb:file:home/nonfrt/Downloads";
    
    @Before
    public void setUp() {
    }

    @Test
    public void testEndConnection() throws SQLException{
        DbConnector connector = new DbConnector(testConnectionString);
        connector.endConnection();
        Assert.assertTrue(connector.getConnection().isClosed());
    }

    @Test
    public void getConnection_GetsConnection() {
        DbConnector connector = new DbConnector(testConnectionString);
        Connection connection = connector.getConnection();
        Assert.assertNotNull(connection);
    }
    
    @Test(expected = DatabaseConnectionFailedException.class)
    public void constructor_ThrowsException_WhenInvalidConnectionString() {
        DbConnector connector = new DbConnector("");
    }
    
}


package retrospector.hsqldb.datagateway;

import retrospector.hsqldb.exceptions.ForeignEntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import retrospector.core.entity.Media;
import retrospector.core.entity.Factoid;
import retrospector.hsqldb.exceptions.EntityNotFoundException;
import retrospector.hsqldb.exceptions.TableCreationQueryFailedException;

public class FactoidGatewayTest {

    private DbConnector connector;
    private FactoidGateway factoidGateway;
    private ResultSetHandler<List<Factoid>> factoidResultHandler;
    private int invalidMediaId = 314;
    
    @Before
    public void setUp() {
        DbConnectorTest.deleteLockFile();
        connector = new DbConnector(DbConnectorTest.testConnectionString);
        DbConnectorTest.clearDatabase(connector);
        
        MediaGateway mediaGateway = new MediaGateway(connector);
        mediaGateway.createMediaTableIfDoesNotExist();
        mediaGateway.add(new Media());
        mediaGateway.add(new Media());
        
        factoidGateway = new FactoidGateway(connector);
        factoidResultHandler = new BeanListHandler<>(Factoid.class);
        factoidGateway.createFactoidTableIfDoesNotExist();
    }
    
    @After
    public void tearDown() {
        connector.exit();
    }

    @Test(expected=TableCreationQueryFailedException.class)
    public void createFactoidTableIfDoesNotExist_ThrowsException_WhenTableIsNotCreate() {
        connector.exit();
        
        factoidGateway.createFactoidTableIfDoesNotExist();
    }
    
    @Test
    public void createFactoidTableIfDoesNotExist_CreatesTable() {
        factoidGateway.createFactoidTableIfDoesNotExist();
        
        List<String> tables = connector.select(new ColumnListHandler<>(), "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES");
        assertTrue(tables.contains("FACTOID"));
        List<String> columns = connector.select(new ColumnListHandler<>(), "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='FACTOID'");
        assertTrue(columns.contains("ID"));
        assertTrue(columns.contains("MEDIAID"));
        assertTrue(columns.contains("TITLE"));
        assertTrue(columns.contains("CONTENT"));
    }

    @Test
    public void addFactoid_AddsFactoid(){
        Factoid factoid = getNewFactoid();
        
        Factoid returnedFactoid = factoidGateway.add(factoid);
        
        List<Factoid> results = connector.select(factoidResultHandler, "select ID from factoid where CONTENT=?", factoid.getContent());
        factoid.setId(results.get(0).getId());
        verifyFactoidAreSame(factoid, returnedFactoid);
    }    
    @Test(expected = ForeignEntityNotFoundException.class)
    public void addFactoid_WhenNoMediaId_ThrowsException() {
        Factoid factoid = getNewFactoid();
        factoid.setMediaId(null);
        
        factoidGateway.add(factoid);
    }
    
    @Test(expected = ForeignEntityNotFoundException.class)
    public void addFactoid_WhenNoMedia_ThrowsException() {
        Factoid factoid = getNewFactoid();
        factoid.setMediaId(invalidMediaId);
        
        factoidGateway.add(factoid);
    }
    
    @Test
    public void getFactoid_GetsFactoid() {
        Factoid factoid = getNewFactoid();
        int id = factoidGateway.add(factoid).getId();
        factoid.setId(id);
        
        Factoid returnedFactoid = factoidGateway.get(factoid.getId());
        
        verifyFactoidAreSame(factoid, returnedFactoid);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void getFactoid_WhenNoFactoidFound_ThrowsException() {
        factoidGateway.get(invalidMediaId);
    }
    
    @Test
    public void updateFactoid_UpdatesFactoid() {        
        Factoid factoid = getNewFactoid();
        factoid = factoidGateway.add(factoid);
        factoid.setMediaId(factoid.getMediaId()+1);
        factoid.setTitle(factoid.getTitle() + "not same");
        factoid.setContent(factoid.getContent());
        
        Factoid returnedFactoid = factoidGateway.update(factoid);
        
        verifyFactoidAreSame(factoid, returnedFactoid);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void deleteFactoid_DeletesFactoid() {
        Factoid factoid = getNewFactoid();
        factoid = factoidGateway.add(factoid);
        
        factoidGateway.delete(factoid.getId());
        
        factoidGateway.get(factoid.getId());
    }
    
    @Test
    public void deleteFactoid_WhenNoFactoidFound_SilentlyFails() {
        factoidGateway.delete(invalidMediaId);
    }
    
    @Test
    public void getFactoids_GetsFactoids() {
        List<Factoid> list = new ArrayList<>();
        list.add(factoidGateway.add(getNewFactoid()));
        list.add(factoidGateway.add(getNewFactoid()));
        list.add(factoidGateway.add(getNewFactoid()));
        
        List<Factoid> returnedList = factoidGateway.getAll();
        
        assertNotNull(returnedList);
        assertTrue(list.size() == returnedList.size());
        list.sort( (x,y) -> x.getId().compareTo(y.getId()));
        returnedList.sort( (x,y) -> x.getId().compareTo(y.getId()));
        for (int i = 0; i < list.size(); i++)
            verifyFactoidAreSame(list.get(i), returnedList.get(i));
    }
    
    @Test
    public void getFactoids_WhenNoFactoidFound_ReturnsEmptyList() {
        List<Factoid> list = factoidGateway.getAll();
        
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }    
    
    @Test
    public void getFactoidsById_GetsFactoids() {
        int mediaId = 1;
        List<Factoid> list = new ArrayList<>();
        list.add(factoidGateway.add(getNewFactoid()));
        list.add(factoidGateway.add(getNewFactoid()));
        list.add(factoidGateway.add(getNewFactoid()));
        Factoid factoid = getNewFactoid();
        factoid.setMediaId(mediaId + 1);
        factoidGateway.add(factoid);
        
        List<Factoid> returnedList = factoidGateway.getByMediaId(mediaId);
        
        assertNotNull(returnedList);
        assertEquals(list.size(), returnedList.size());
        list.sort( (x,y) -> x.getId().compareTo(y.getId()));
        returnedList.sort( (x,y) -> x.getId().compareTo(y.getId()));
        for (int i = 0; i < list.size(); i++)
            verifyFactoidAreSame(list.get(i), returnedList.get(i));
    }
    
    @Test
    public void getFactoidsById_WhenNoFactoidFound_ReturnsEmptyList() {
        List<Factoid> list = factoidGateway.getByMediaId(invalidMediaId);
        
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
    
    private Factoid getNewFactoid() {
        Factoid factoid = new Factoid();
        factoid.setMediaId(1);
        factoid.setTitle("Genre");
        factoid.setContent(java.util.UUID.randomUUID().toString());
        return factoid;
    }
    
    private void verifyFactoidAreSame(Factoid factoid, Factoid returnedFactoid) {
        if (factoid == returnedFactoid)
            return;
        assertNotNull(factoid);
        assertNotNull(returnedFactoid);
        assertEquals(factoid.getId(), returnedFactoid.getId());
        assertEquals(factoid.getMediaId(), returnedFactoid.getMediaId());
        assertEquals(factoid.getTitle(), returnedFactoid.getTitle());
        assertEquals(factoid.getContent(), returnedFactoid.getContent());
    }
}

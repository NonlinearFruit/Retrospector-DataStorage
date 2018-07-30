
package retrospector.hsqldb.datagateway;

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
    
    @Before
    public void setUp() {
        connector = new DbConnector(DbConnectorTest.testConnectionString);
        
        DbConnectorTest.deleteLockFile();
        DbConnectorTest.clearDatabase(connector);
        
        MediaGateway mediaGateway = new MediaGateway(connector);
        mediaGateway.createMediaTableIfDoesNotExist();
        mediaGateway.addMedia(new Media());
        mediaGateway.addMedia(new Media());
        
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
        
        Factoid returnedFactoid = factoidGateway.addFactoid(factoid);
        
        List<Factoid> results = connector.select(factoidResultHandler, "select ID from factoid where CONTENT=?", factoid.getContent());
        factoid.setId(results.get(0).getId());
        verifyFactoidAreSame(factoid, returnedFactoid);
    }
    
    @Test
    public void getFactoid_GetsFactoid() {
        Factoid factoid = getNewFactoid();
        int id = factoidGateway.addFactoid(factoid).getId();
        factoid.setId(id);
        
        Factoid returnedFactoid = factoidGateway.getFactoid(factoid.getId());
        
        verifyFactoidAreSame(factoid, returnedFactoid);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void getFactoid_WhenNoFactoidFound_ThrowsException() {
        factoidGateway.getFactoid(314);
    }
    
    @Test
    public void updateFactoid_UpdatesFactoid() {        
        Factoid factoid = getNewFactoid();
        factoid = factoidGateway.addFactoid(factoid);
        factoid.setMediaId(factoid.getMediaId()+1);
//        factoid.setDate(factoid.getDate().plusDays(1));
//        factoid.setRating(factoid.getRating()+1);
//        factoid.setFactoid(factoid.getFactoid() + "not same");
//        factoid.setUser(factoid.getUser() + "not same");
        
        Factoid returnedFactoid = factoidGateway.updateFactoid(factoid);
        
        verifyFactoidAreSame(factoid, returnedFactoid);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void deleteFactoid_DeletesFactoid() {
        Factoid factoid = getNewFactoid();
        factoid = factoidGateway.addFactoid(factoid);
        
        factoidGateway.deleteFactoid(factoid.getId());
        
        factoidGateway.getFactoid(factoid.getId());
    }
    
    @Test
    public void deleteFactoid_WhenNoFactoidFound_SilentlyFails() {
        factoidGateway.deleteFactoid(314);
    }
    
    @Test
    public void getFactoids_GetsFactoids() {
        List<Factoid> list = new ArrayList<>();
        list.add(factoidGateway.addFactoid(getNewFactoid()));
        list.add(factoidGateway.addFactoid(getNewFactoid()));
        list.add(factoidGateway.addFactoid(getNewFactoid()));
        
        List<Factoid> returnedList = factoidGateway.getFactoids();
        
        assertNotNull(returnedList);
        assertTrue(list.size() == returnedList.size());
        list.sort( (x,y) -> x.getId().compareTo(y.getId()));
        returnedList.sort( (x,y) -> x.getId().compareTo(y.getId()));
        for (int i = 0; i < list.size(); i++)
            verifyFactoidAreSame(list.get(i), returnedList.get(i));
    }
    
    @Test
    public void getFactoids_WhenNoFactoidFound_ReturnsEmptyList() {
        List<Factoid> list = factoidGateway.getFactoids();
        
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


package retrospector.hsqldb.datagateway;

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
import retrospector.hsqldb.exceptions.TableCreationQueryFailedException;

public class MediaGatewayTest {

    private DbConnector connector;
    private MediaGateway mediaGateway;
    private ResultSetHandler<List<Media>> mediaResultHandler;
    
    @Before
    public void setUp() {
        DbConnectorTest.deleteLockFile();
        connector = new DbConnector(DbConnectorTest.testConnectionString);
        mediaGateway = new MediaGateway(connector);
        mediaResultHandler = new BeanListHandler<>(Media.class);
        connector.execute("DROP SCHEMA PUBLIC CASCADE");
    }
    
    @After
    public void tearDown() {
        connector.exit();
    }

    @Test(expected=TableCreationQueryFailedException.class)
    public void createMediaTableIfDoesNotExist_ThrowsException_WhenTableIsNotCreate() {
        connector.exit();
        
        mediaGateway.createMediaTableIfDoesNotExist();
    }
    
    @Test
    public void createMediaTableIfDoesNotExist_CreatesTable() {
        mediaGateway.createMediaTableIfDoesNotExist();
        
        List<String> tables = connector.select(new ColumnListHandler<String>(), "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES");
        assertTrue(tables.contains("MEDIA"));
        List<String> columns = connector.select(new ColumnListHandler<String>(), "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='MEDIA'");
        assertTrue(columns.contains("ID"));
        assertTrue(columns.contains("TITLE"));
        assertTrue(columns.contains("CREATOR"));
        assertTrue(columns.contains("SEASON"));
        assertTrue(columns.contains("EPISODE"));
        assertTrue(columns.contains("CATEGORY"));
        assertTrue(columns.contains("TYPE"));
        assertTrue(columns.contains("DESCRIPTION"));
    }

    @Test
    public void addMedia_AddsMedia(){
        Media media = getNewMedia();
        mediaGateway.createMediaTableIfDoesNotExist();
        
        Media returnedMedia = mediaGateway.addMedia(media);
        
        assertNotNull(returnedMedia);
        assertEquals(media.getDescription(), returnedMedia.getDescription());
        List<Media> results = connector.select(mediaResultHandler, "select ID from MEDIA where DESCRIPTION=?", returnedMedia.getDescription());
        assertEquals(returnedMedia.getId(), results.get(0).getId());
    }
    
    private Media getNewMedia() {
        Media media = new Media();
        media.setDescription(java.util.UUID.randomUUID().toString());
        return media;
    }
}

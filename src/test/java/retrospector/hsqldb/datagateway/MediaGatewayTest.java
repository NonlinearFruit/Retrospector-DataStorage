
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
import retrospector.hsqldb.exceptions.EntityNotFoundException;
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
        DbConnectorTest.clearDatabase(connector);
        mediaGateway.createMediaTableIfDoesNotExist();
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
        
        List<String> tables = connector.select(new ColumnListHandler<>(), "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES");
        assertTrue(tables.contains("MEDIA"));
        List<String> columns = connector.select(new ColumnListHandler<>(), "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='MEDIA'");
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
        
        Media returnedMedia = mediaGateway.add(media);
        
        List<Media> results = connector.select(mediaResultHandler, "select ID from MEDIA where DESCRIPTION=?", returnedMedia.getDescription());
        media.setId(results.get(0).getId());
        verifyMediaAreSame(media, returnedMedia);
    }
    
    @Test
    public void getMedia_GetsMedia() {
        Media media = getNewMedia();
        int id = mediaGateway.add(media).getId();
        media.setId(id);
        
        Media returnedMedia = mediaGateway.get(media.getId());
        
        verifyMediaAreSame(media, returnedMedia);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void getMedia_WhenNoMediaFound_ThrowsException() {
        mediaGateway.get(314);
    }
    
    @Test
    public void updateMedia_UpdatesMedia() {        
        Media media = getNewMedia();
        media = mediaGateway.add(media);
        media.setTitle(media.getTitle() + "not same");
        media.setCreator(media.getCreator()+ "not same");
        media.setSeason(media.getSeason() + "not same");
        media.setEpisode(media.getEpisode()+ "not same");
        media.setCategory(media.getCategory()+ "not same");
        media.setType(media.getType() == Media.Type.SERIES ? Media.Type.SINGLE : Media.Type.SERIES);
        media.setDescription(media.getDescription() + "not same");
        
        Media returnedMedia = mediaGateway.update(media);
        
        verifyMediaAreSame(media, returnedMedia);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void deleteMedia_DeletesMedia() {
        Media media = getNewMedia();
        media = mediaGateway.add(media);
        
        mediaGateway.delete(media.getId());
        
        mediaGateway.get(media.getId());
    }
    
    @Test
    public void deleteMedia_WhenNoMediaFound_SilentlyFails() {
        mediaGateway.delete(314);
    }
    
    @Test
    public void getAllMedia_GetsAllMedia() {
        List<Media> list = new ArrayList<>();
        list.add(mediaGateway.add(getNewMedia()));
        list.add(mediaGateway.add(getNewMedia()));
        list.add(mediaGateway.add(getNewMedia()));
        
        List<Media> returnedList = mediaGateway.getAll();
        
        assertNotNull(returnedList);
        assertTrue(list.size() == returnedList.size());
        list.sort( (x,y) -> x.getId().compareTo(y.getId()));
        returnedList.sort( (x,y) -> x.getId().compareTo(y.getId()));
        for (int i = 0; i < list.size(); i++)
            verifyMediaAreSame(list.get(i), returnedList.get(i));
    }
    
    @Test
    public void getAllMedia_WhenNoMediaFound_ReturnsEmptyList() {
        List<Media> list = mediaGateway.getAll();
        
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
    
    private Media getNewMedia() {
        Media media = new Media();
        media.setTitle("1729");
        media.setCreator("Arthur Doyle");
        media.setSeason("S5");
        media.setEpisode("E5");
        media.setCategory("Book");
        media.setDescription(java.util.UUID.randomUUID().toString());
        media.setType(Media.Type.SERIES);
        return media;
    }
    
    private void verifyMediaAreSame(Media media, Media returnedMedia) {
        if (media == returnedMedia)
            return;
        assertNotNull(media);
        assertNotNull(returnedMedia);
        assertEquals(media.getId(), returnedMedia.getId());
        assertEquals(media.getTitle(), returnedMedia.getTitle());
        assertEquals(media.getCreator(), returnedMedia.getCreator());
        assertEquals(media.getSeason(), returnedMedia.getSeason());
        assertEquals(media.getEpisode(), returnedMedia.getEpisode());
        assertEquals(media.getCategory(), returnedMedia.getCategory());
        assertEquals(media.getType(), returnedMedia.getType());
        assertEquals(media.getDescription(), returnedMedia.getDescription());
    }
}

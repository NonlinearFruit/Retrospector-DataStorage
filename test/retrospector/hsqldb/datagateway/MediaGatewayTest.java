
package retrospector.hsqldb.datagateway;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import retrospector.core.entity.Media;
import retrospector.hsqldb.exceptions.TableCreationQueryFailedException;

public class MediaGatewayTest {

    private DbConnector connector;
    private MediaGateway mediaGateway;
    
    @Before
    public void setUp() {
        connector = new DbConnector(DbConnectorTest.testConnectionString);
        mediaGateway = new MediaGateway(connector);
    }

    @Test(expected=TableCreationQueryFailedException.class)
    public void createMediaTableIfDoesNotExist_ThrowsException_WhenTableIsNotCreate() {
        connector.endConnection();
        mediaGateway.createMediaTableIfDoesNotExist();
    }

    @Test
    public void addMedia() {
        fail("The test case is a prototype.");
    }
    
    private Media getNewMedia() {
        Media media = new Media();
        media.setId(Integer.SIZE);
        return media;
    }
}

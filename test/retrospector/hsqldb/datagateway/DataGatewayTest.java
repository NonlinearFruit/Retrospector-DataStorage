
package retrospector.hsqldb.datagateway;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import retrospector.core.datagateway.DataGateway;

@RunWith(MockitoJUnitRunner.class)
public class DataGatewayTest {
    
    @Mock
    private MediaGateway mediaGateway;
    @Mock
    private ReviewGateway reviewGateway;
    @Mock
    private FactoidGateway factoidGateway;

    @Before
    public void setup() {
        DataGateway dataGateway = new CrudGateway(mediaGateway, reviewGateway, factoidGateway);
    }
}

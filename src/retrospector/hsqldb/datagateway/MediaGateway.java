
package retrospector.hsqldb.datagateway;

import java.sql.SQLException;
import retrospector.core.entity.Media;
import retrospector.hsqldb.exceptions.TableCreationQueryFailedException;

public class MediaGateway {
   
    private DbConnector connector;
    
    public MediaGateway(DbConnector connector) {
        this.connector = connector;
    }
    
    public void createMediaTableIfDoesNotExist() {
        String createMedia = ""
        + "create table if not exists media ("
        + "id integer not null generated always as identity (start with 1, increment by 1),   "
        + "title varchar(1000000),"
        + "creator varchar(1000000),"
        + "season varchar(1000000),"
        + "episode varchar(1000000),"
        + "description varchar(1000000),"
        + "category varchar(1000000),"
        + "type varchar(1000000),"
        + "constraint primary_key_media primary key (id))";
        
        try {
            connector.getConnection().createStatement().execute(createMedia);
        } catch (SQLException ex) {
//            throw new TableCreationQueryFailedException();
        }
    }
    
    public Media addMedia(Media media) {
        return null;
    }
}

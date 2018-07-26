
package retrospector.hsqldb.datagateway;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.ResultSetHandler;
import retrospector.core.entity.Media;
import retrospector.hsqldb.exceptions.QueryFailedException;
import retrospector.hsqldb.exceptions.TableCreationQueryFailedException;

public class MediaGateway {
   
    private DbConnector connector;
    private ResultSetHandler<List<Media>> mediaResultHandler;
    
    public MediaGateway(DbConnector connector) {
        this.connector = connector;
        this.mediaResultHandler = new ResultSetHandler<List<Media>>(){
            @Override
            public List<Media> handle(ResultSet rs) throws SQLException {
                List<Media> list = new ArrayList<>();
                while(rs.next()) {
                    Media medium = new Media();
                    medium.setId(rs.getInt("id"));
                    medium.setTitle(rs.getString("title"));
                    medium.setCreator(rs.getString("creator"));
                    medium.setSeason(rs.getString("season"));
                    medium.setEpisode(rs.getString("episode"));
                    medium.setDescription(rs.getString("description"));
                    medium.setCategory(rs.getString("category"));
                    medium.setType(Media.Type.valueOf(rs.getString("type")));
                    list.add(medium);
                }
                return list;
            }
        };
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
        connector.execute(createMedia);
        } catch(QueryFailedException ex) {
            throw new TableCreationQueryFailedException(ex);
        }
    }
    
    public Media addMedia(Media media) {
        return connector.insert(mediaResultHandler, "INSERT INTO MEDIA(DESCRIPTION) VALUES (?)", media.getDescription()).get(0);
    }
}


package retrospector.hsqldb.datagateway;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import retrospector.core.datagateway.CrudDataGateway;
import retrospector.core.entity.Media;
import retrospector.hsqldb.exceptions.EntityNotFoundException;
import retrospector.hsqldb.exceptions.QueryFailedException;
import retrospector.hsqldb.exceptions.TableCreationQueryFailedException;

public class MediaGateway implements CrudDataGateway<Media> {
  
  private DataConnector connector;
  private ResultSetHandler<Media> mediaResultHandler;
  private ResultSetHandler<Integer> mediaIdResultHandler;
  private ResultSetHandler<List<Media>> mediaListResultHandler;
  
  public MediaGateway(DataConnector connector) {
    this.connector = connector;
    this.mediaResultHandler = new BeanHandler<>(Media.class);
    this.mediaIdResultHandler = new ScalarHandler<>();
    this.mediaListResultHandler = new ResultSetHandler<List<Media>>(){
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
  
  @Override
  public Media add(Media media) {
    return get(connector.insert(mediaIdResultHandler, "INSERT INTO media(title, creator, season, episode, category, type, description) VALUES (?,?,?,?,?,?,?)", 
      media.getTitle(),
      media.getCreator(),
      media.getSeason(),
      media.getEpisode(),
      media.getCategory(),
      media.getType().toString(),
      media.getDescription()
    ));
  }
  
  @Override
  public Media get(int mediaId) {
    Media media = connector.select(mediaResultHandler, "SELECT * FROM media WHERE id=?", mediaId);
    if (media == null)
      throw new EntityNotFoundException();
    return media;
  }

  @Override
  public Media update(Media media) {
    connector.execute("UPDATE media SET title=?,creator=?,season=?,episode=?,description=?,category=?,type=? where id=?",
      media.getTitle(),
      media.getCreator(),
      media.getSeason(),
      media.getEpisode(),
      media.getDescription(),
      media.getCategory(),
      media.getType().toString(),
      media.getId()
    );
    return get(media.getId());
  }

  @Override
  public void delete(int mediaId) {
    connector.execute("delete from media where id=?", mediaId);
  }

  @Override
  public List<Media> getAll() {
    return connector.select(mediaListResultHandler, "SELECT * FROM media");
  }

  @Override
  public List<Media> getByMediaId(int mediaId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}

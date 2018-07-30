
package retrospector.hsqldb.datagateway;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import retrospector.core.entity.Factoid;
import retrospector.hsqldb.exceptions.EntityNotFoundException;
import retrospector.hsqldb.exceptions.QueryFailedException;
import retrospector.hsqldb.exceptions.TableCreationQueryFailedException;

public class FactoidGateway {
   
    private DbConnector connector;
    private ResultSetHandler<Factoid> factoidResultHandler;
    private ResultSetHandler<Integer> factoidIdResultHandler;
    private ResultSetHandler<List<Factoid>> factoidListResultHandler;
    
    public FactoidGateway(DbConnector connector) {
        this.connector = connector;
        this.factoidIdResultHandler = new ScalarHandler<>();
        this.factoidResultHandler = new ResultSetHandler<Factoid>(){
            @Override
            public Factoid handle(ResultSet rs) throws SQLException {
                if (!rs.next())
                    return null;
                Factoid factoid = new Factoid();
                factoid.setId(rs.getInt("id"));
                factoid.setMediaId(rs.getInt("mediaId"));
                factoid.setTitle(rs.getString("title"));
                factoid.setContent(rs.getString("content"));
                return factoid;
            }
        };
        this.factoidListResultHandler = new ResultSetHandler<List<Factoid>>(){
            @Override
            public List<Factoid> handle(ResultSet rs) throws SQLException {
                List<Factoid> list = new ArrayList<>();
                Factoid factoid = factoidResultHandler.handle(rs);
                while(factoid != null) {
                    list.add(factoid);
                    factoid = factoidResultHandler.handle(rs);
                }
                return list;
            }
        };
    }
    
    public void createFactoidTableIfDoesNotExist() {
        String createFactoidTable = ""
        + "create table if not exists factoid ("
        + "id integer not null generated always as identity (start with 1, increment by 1),   "
        + "mediaID integer not null,   "
        + "title varchar(1000000),"
        + "content varchar(1000000),"
        + "constraint primary_key_factoid primary key (id),"
        + "constraint foreign_key_factoid foreign key (mediaID) references media (id) on delete cascade)";
        
        try {
            connector.execute(createFactoidTable);
        } catch(QueryFailedException ex) {
            throw new TableCreationQueryFailedException(ex);
        }
    }
    
    public Factoid addFactoid(Factoid factoid) {
        return getFactoid(connector.insert(factoidIdResultHandler, "INSERT INTO factoid(mediaId, title, content) VALUES (?,?,?)",
                factoid.getMediaId(),
                factoid.getTitle(),
                factoid.getContent()
        ));
    }
    
    public Factoid getFactoid(int factoidId) {
        Factoid factoid = connector.select(factoidResultHandler, "SELECT * FROM factoid WHERE id=?", factoidId);
        if (factoid == null)
            throw new EntityNotFoundException();
        return factoid;
    }

    public Factoid updateFactoid(Factoid factoid) {
        connector.execute("UPDATE factoid SET mediaId=?, title=?, content=?",
                factoid.getMediaId(),
                factoid.getTitle(),
                factoid.getContent()
        );
        return getFactoid(factoid.getId());
    }

    void deleteFactoid(int factoidId) {
        connector.execute("DELETE FROM factoid WHERE id=?", factoidId);
    }

    List<Factoid> getFactoids() {
        return connector.select(factoidListResultHandler, "SELECT * FROM factoid");
    }
}

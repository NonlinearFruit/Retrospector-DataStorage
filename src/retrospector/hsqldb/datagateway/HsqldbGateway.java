
package retrospector.hsqldb.datagateway;

import static deleteme.DataManager.createDB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import retrospector.core.datagateway.DataGateway;
import retrospector.core.entity.Factoid;
import retrospector.core.entity.Media;
import retrospector.core.entity.Review;

public class HsqldbGateway implements DataGateway{
    private DbConnector connector;
    private PropertyGateway propertyGateway;
    
    public HsqldbGateway(PropertyGateway propertyGateway, DbConnector connection) {
        this.propertyGateway = propertyGateway;
        this.connector = connection;
        startDB();
    }
    
    private void startDB(){
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
      
        String createReview = ""
        + "create table if not exists review ("
        + "id integer not null generated always as identity (start with 1, increment by 1),   "
        + "mediaID integer not null,   "
        + "reviewer varchar(1000000),"
        + "date date,"
        + "review varchar(1000000),"
        + "rate int,"
        + "constraint primary_key_review primary key (id),"
        + "constraint foreign_key_review foreign key (mediaID) references media (id) on delete cascade)";
        
        String createFactoid = ""
        + "create table if not exists factoid ("
        + "id integer not null generated always as identity (start with 1, increment by 1),   "
        + "mediaID integer not null,   "
        + "title varchar(1000000),"
        + "content varchar(1000000),"
        + "constraint primary_key_factoid primary key (id),"
        + "constraint foreign_key_factoid foreign key (mediaID) references media (id) on delete cascade)";
        
        try {
            Statement stmt;
        
            stmt = connector.getConnection().createStatement();
            
            stmt.execute(createMedia);
            
            stmt.execute(createReview);
            
            stmt.execute(createFactoid);
            
        } catch (SQLException ex) {
            System.err.println("Create error in startDB in connection" + ex);
        }
    }
    
    @Override
    public Media addMedia(Media media) {
        int id = -1;
        try {
            PreparedStatement pstmt;

            pstmt = connector.getConnection().prepareStatement(
                    "insert into media(title,creator,season,episode,description,category,type) values(?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, media.getTitle());
            pstmt.setString(2, media.getCreator());
            pstmt.setString(3, media.getSeason());
            pstmt.setString(4, media.getEpisode());
            pstmt.setString(5, media.getDescription());
            pstmt.setString(6, media.getCategory());
            pstmt.setString(7, media.getType().toString());
            int updated = pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            boolean key = rs.next();
            if (updated == 1 && key) {
                id = rs.getInt(1);
                for (Review review : media.getReviews())
                    addReview(review);
                for (Factoid factoid : media.getFactoids())
                    addFactoid(factoid);
            } else if (media.getFactoids().size() > 0 || media.getReviews().size() > 0) {
                System.err.println("Reviews/Factoids not Saved! :(");
            }
        } catch (SQLException ex) {
            System.err.println("createDB error in connection" + ex);
        }
        return getMedia(id);
    }

    @Override
    public Media getMedia(int mediaId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Media updateMedia(Media media) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteMedia(int mediaId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Review addReview(Review review) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Review getReview(int reviewId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Review updateReview(Review review) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteReview(int reviewId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Factoid addFactoid(Factoid factoid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Factoid getFactoid(int factoidId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Factoid updateFactoid(Factoid factoid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteFactoid(int factoidId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

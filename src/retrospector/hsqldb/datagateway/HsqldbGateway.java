
package retrospector.hsqldb.datagateway;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import retrospector.core.datagateway.DataGateway;
import retrospector.core.entity.Factoid;
import retrospector.core.entity.Media;
import retrospector.core.entity.Review;
import retrospector.core.request.model.RequestableReview;

public class HsqldbGateway implements DataGateway{
    private DbConnector connector;
    private PropertyGateway propertyGateway;
    
    public HsqldbGateway(PropertyGateway propertyGateway, DbConnector connection) {
        this.propertyGateway = propertyGateway;
        this.connector = connection;
        startDB();
    }
    
    private void startDB(){
      
        String createFactoid = ""
        + "create table if not exists factoid ("
        + "id integer not null generated always as identity (start with 1, increment by 1),   "
        + "mediaID integer not null,   "
        + "title varchar(1000000),"
        + "content varchar(1000000),"
        + "constraint primary_key_factoid primary key (id),"
        + "constraint foreign_key_factoid foreign key (mediaID) references media (id) on delete cascade)";
        
        connector.execute(createFactoid);
    }
    
    @Override
    public Media addMedia(Media media) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    @Override
    public void reviewRetrieved(RequestableReview requestableReview) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

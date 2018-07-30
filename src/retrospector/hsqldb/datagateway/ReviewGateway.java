
package retrospector.hsqldb.datagateway;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import retrospector.core.entity.Review;
import retrospector.hsqldb.exceptions.EntityNotFoundException;
import retrospector.hsqldb.exceptions.QueryFailedException;
import retrospector.hsqldb.exceptions.TableCreationQueryFailedException;

public class ReviewGateway {
   
    private DbConnector connector;
    private ResultSetHandler<Review> reviewResultHandler;
    private ResultSetHandler<Integer> reviewIdResultHandler;
    private ResultSetHandler<List<Review>> reviewListResultHandler;
    
    public ReviewGateway(DbConnector connector) {
        this.connector = connector;
        this.reviewIdResultHandler = new ScalarHandler<>();
        this.reviewResultHandler = new ResultSetHandler<Review>(){
            @Override
            public Review handle(ResultSet rs) throws SQLException {
                if (!rs.next())
                    return null;
                Review review = new Review();
                review.setId(rs.getInt("id"));
                review.setMediaId(rs.getInt("mediaId"));
                review.setUser(rs.getString("reviewer"));
                review.setDate(rs.getDate("date").toLocalDate());
                review.setReview(rs.getString("review"));
                review.setRating(rs.getInt("rating"));
                return review;
            }
        };
        this.reviewListResultHandler = new ResultSetHandler<List<Review>>(){
            @Override
            public List<Review> handle(ResultSet rs) throws SQLException {
                List<Review> list = new ArrayList<>();
                Review review = reviewResultHandler.handle(rs);
                while(review != null) {
                    list.add(review);
                    review = reviewResultHandler.handle(rs);
                }
                return list;
            }
        };
    }
    
    public void createReviewTableIfDoesNotExist() {
        String createReviewTable = ""
        + "create table if not exists review ("
        + "id integer not null generated always as identity (start with 1, increment by 1),   "
        + "mediaId integer not null,   "
        + "reviewer varchar(1000000),"
        + "date date,"
        + "review varchar(1000000),"
        + "rating int,"
        + "constraint primary_key_review primary key (id),"
        + "constraint foreign_key_review foreign key (mediaID) references media (id) on delete cascade)";
        
        try {
            connector.execute(createReviewTable);
        } catch(QueryFailedException ex) {
            throw new TableCreationQueryFailedException(ex);
        }
    }
    
    public Review addReview(Review review) {
        try {
            return getReview(connector.insert(reviewIdResultHandler, "INSERT INTO review(mediaId, rating, date, reviewer, review) VALUES (?,?,?,?,?)",
                    review.getMediaId(),
                    review.getRating(),
                    Date.valueOf(review.getDate()),
                    review.getUser(),
                    review.getReview()
            ));
        } catch(QueryFailedException qex) {
            if (review.getMediaId() == null || qex.getMessage().contains("foreign key no parent"))
                throw new ForeignEntityNotFoundException();
            throw qex;
        }
    }
    
    public Review getReview(int reviewId) {
        Review review = connector.select(reviewResultHandler, "SELECT * FROM review WHERE id=?", reviewId);
        if (review == null)
            throw new EntityNotFoundException();
        return review;
    }

    public Review updateReview(Review review) {
        connector.execute("UPDATE review SET mediaId=?, rating=?, date=?, reviewer=?, review=?",
                review.getMediaId(),
                review.getRating(),
                Date.valueOf(review.getDate()),
                review.getUser(),
                review.getReview()
        );
        return getReview(review.getId());
    }

    void deleteReview(int reviewId) {
        connector.execute("DELETE FROM review WHERE id=?", reviewId);
    }

    List<Review> getReviews() {
        return connector.select(reviewListResultHandler, "SELECT * FROM review");
    }
}

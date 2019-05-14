
package retrospector.hsqldb.datagateway;

import retrospector.hsqldb.exceptions.ForeignEntityNotFoundException;
import java.time.LocalDate;
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
import retrospector.core.entity.Review;
import retrospector.hsqldb.exceptions.EntityNotFoundException;
import retrospector.hsqldb.exceptions.TableCreationQueryFailedException;

public class ReviewGatewayTest {

    private DbConnector connector;
    private ReviewGateway reviewGateway;
    private ResultSetHandler<List<Review>> reviewResultHandler;
    private int invalidMediaId = 314;
    
    @Before
    public void setUp() {
        DbConnectorTest.deleteLockFile();
        connector = new DbConnector(DbConnectorTest.testConnectionString);
        DbConnectorTest.clearDatabase(connector);
        
        MediaGateway mediaGateway = new MediaGateway(connector);
        mediaGateway.createMediaTableIfDoesNotExist();
        mediaGateway.add(new Media());
        mediaGateway.add(new Media());
        
        reviewGateway = new ReviewGateway(connector);
        reviewResultHandler = new BeanListHandler<>(Review.class);
        reviewGateway.createReviewTableIfDoesNotExist();
    }
    
    @After
    public void tearDown() {
        connector.exit();
    }

    @Test(expected=TableCreationQueryFailedException.class)
    public void createReviewTableIfDoesNotExist_ThrowsException_WhenTableIsNotCreate() {
        connector.exit();
        
        reviewGateway.createReviewTableIfDoesNotExist();
    }
    
    @Test
    public void createReviewTableIfDoesNotExist_CreatesTable() {
        reviewGateway.createReviewTableIfDoesNotExist();
        
        List<String> tables = connector.select(new ColumnListHandler<>(), "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES");
        assertTrue(tables.contains("REVIEW"));
        List<String> columns = connector.select(new ColumnListHandler<>(), "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='REVIEW'");
        assertTrue(columns.contains("ID"));
        assertTrue(columns.contains("RATING"));
        assertTrue(columns.contains("REVIEWER"));
        assertTrue(columns.contains("REVIEW"));
        assertTrue(columns.contains("DATE"));
        assertTrue(columns.contains("MEDIAID"));
    }

    @Test
    public void addReview_AddsReview(){
        Review review = getNewReview();
        
        Review returnedReview = reviewGateway.add(review);
        
        List<Review> results = connector.select(reviewResultHandler, "select ID from REVIEW where REVIEW=?", review.getReview());
        review.setId(results.get(0).getId());
        verifyReviewAreSame(review, returnedReview);
    }
    
    @Test(expected = ForeignEntityNotFoundException.class)
    public void addReview_WhenNoMediaId_ThrowsException() {
        Review review = getNewReview();
        review.setMediaId(null);
        
        reviewGateway.add(review);
    }
    
    @Test(expected = ForeignEntityNotFoundException.class)
    public void addReview_WhenNoMedia_ThrowsException() {
        Review review = getNewReview();
        review.setMediaId(invalidMediaId);
        
        reviewGateway.add(review);
    }
    
    @Test
    public void getReview_GetsReview() {
        Review review = getNewReview();
        int id = reviewGateway.add(review).getId();
        review.setId(id);
        
        Review returnedReview = reviewGateway.get(review.getId());
        
        verifyReviewAreSame(review, returnedReview);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void getReview_WhenNoReviewFound_ThrowsException() {
        reviewGateway.get(invalidMediaId);
    }
    
    @Test
    public void updateReview_UpdatesReview() {        
        Review review = getNewReview();
        review = reviewGateway.add(review);
        review.setMediaId(review.getMediaId()+1);
        review.setDate(review.getDate().plusDays(1));
        review.setRating(review.getRating()+1);
        review.setReview(review.getReview() + "not same");
        review.setUser(review.getUser() + "not same");
        
        Review returnedReview = reviewGateway.update(review);
        
        verifyReviewAreSame(review, returnedReview);
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void deleteReview_DeletesReview() {
        Review review = getNewReview();
        review = reviewGateway.add(review);
        
        reviewGateway.delete(review.getId());
        
        reviewGateway.get(review.getId());
    }
    
    @Test
    public void deleteReview_WhenNoReviewFound_SilentlyFails() {
        reviewGateway.delete(invalidMediaId);
    }
    
    @Test
    public void getReviews_GetsReviews() {
        List<Review> list = new ArrayList<>();
        list.add(reviewGateway.add(getNewReview()));
        list.add(reviewGateway.add(getNewReview()));
        list.add(reviewGateway.add(getNewReview()));
        
        List<Review> returnedList = reviewGateway.getAll();
        
        assertNotNull(returnedList);
        assertTrue(list.size() == returnedList.size());
        list.sort( (x,y) -> x.getId().compareTo(y.getId()));
        returnedList.sort( (x,y) -> x.getId().compareTo(y.getId()));
        for (int i = 0; i < list.size(); i++)
            verifyReviewAreSame(list.get(i), returnedList.get(i));
    }
    
    @Test
    public void getReviews_WhenNoReviewFound_ReturnsEmptyList() {
        List<Review> list = reviewGateway.getAll();
        
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
    
    @Test
    public void getReviewsById_GetsReviews() {
        int mediaId = 1;
        List<Review> list = new ArrayList<>();
        list.add(reviewGateway.add(getNewReview()));
        list.add(reviewGateway.add(getNewReview()));
        list.add(reviewGateway.add(getNewReview()));
        Review review = getNewReview();
        review.setMediaId(mediaId + 1);
        reviewGateway.add(review);
        
        List<Review> returnedList = reviewGateway.getByMediaId(mediaId);
        
        assertNotNull(returnedList);
        assertEquals(list.size(), returnedList.size());
        list.sort( (x,y) -> x.getId().compareTo(y.getId()));
        returnedList.sort( (x,y) -> x.getId().compareTo(y.getId()));
        for (int i = 0; i < list.size(); i++)
            verifyReviewAreSame(list.get(i), returnedList.get(i));
    }
    
    @Test
    public void getReviewsById_WhenNoReviewFound_ReturnsEmptyList() {
        List<Review> list = reviewGateway.getByMediaId(invalidMediaId);
        
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
    
    private Review getNewReview() {
        Review review = new Review();
        review.setRating(1729);
        review.setUser("Arthur Doyle");
        review.setMediaId(1);
        review.setDate(LocalDate.now());
        review.setReview(java.util.UUID.randomUUID().toString());
        return review;
    }
    
    private void verifyReviewAreSame(Review review, Review returnedReview) {
        if (review == returnedReview)
            return;
        assertNotNull(review);
        assertNotNull(returnedReview);
        assertEquals(review.getUser(), returnedReview.getUser());
        assertEquals(review.getMediaId(), returnedReview.getMediaId());
        assertEquals(review.getRating(), returnedReview.getRating());
        assertEquals(review.getDate(), returnedReview.getDate());
        assertEquals(review.getReview(), returnedReview.getReview());
        assertEquals(review.getId(), returnedReview.getId());
    }
}

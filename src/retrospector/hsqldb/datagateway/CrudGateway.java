
package retrospector.hsqldb.datagateway;

import java.util.List;
import retrospector.core.datagateway.DataGateway;
import retrospector.core.entity.Factoid;
import retrospector.core.entity.Media;
import retrospector.core.entity.Review;

public class CrudGateway implements DataGateway {

    private MediaGateway mediaGateway;
    private ReviewGateway reviewGateway;
    private FactoidGateway factoidGateway;
    
    public CrudGateway(MediaGateway mediaGateway, ReviewGateway reviewGateway, FactoidGateway factoidGateway) {
        this.mediaGateway = mediaGateway;
        this.reviewGateway = reviewGateway;
        this.factoidGateway = factoidGateway;
        
        mediaGateway.createMediaTableIfDoesNotExist();
        reviewGateway.createReviewTableIfDoesNotExist();
        factoidGateway.createFactoidTableIfDoesNotExist();
    }

    @Override
    public Media addMedia(Media media) {
        media = mediaGateway.addMedia(media);
        for (Review review : media.getReviews()) {
            review.setMediaId(media.getId());
            reviewGateway.addReview(review);
        }
        for (Factoid factoid : media.getFactoids()) {
            factoid.setMediaId(media.getId());
            factoidGateway.addFactoid(factoid);
        }
        return media;
    }

    @Override
    public Media getMedia(int mediaId) {
        Media media = mediaGateway.getMedia(mediaId);
        media.getReviews().addAll(reviewGateway.getReviews(mediaId));
        media.getFactoids().addAll(factoidGateway.getFactoids(mediaId));
        return media;
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
    public List<Media> getMedia() {
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
    public List<Review> getReviews() {
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
    public List<Factoid> getFactoids() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

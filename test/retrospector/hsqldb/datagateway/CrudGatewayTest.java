package retrospector.hsqldb.datagateway;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.Captor;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import retrospector.core.entity.Factoid;
import retrospector.core.entity.Media;
import retrospector.core.entity.Review;

@RunWith(MockitoJUnitRunner.class)
public class CrudGatewayTest {
    @Mock
    private MediaGateway mediaGateway;
    @Mock
    private ReviewGateway reviewGateway;
    @Mock
    private FactoidGateway factoidGateway;
    @Captor
    private ArgumentCaptor<Media> mediaCaptor;
    @Captor
    private ArgumentCaptor<Review> reviewCaptor;
    @Captor
    private ArgumentCaptor<Factoid> factoidCaptor;
    
    private CrudGateway crudGateway;
    private Integer mediaId = 314;
    
    @Before
    public void setUp() {
        crudGateway = new CrudGateway(mediaGateway, reviewGateway, factoidGateway);
        when(mediaGateway.addMedia(any())).thenAnswer(invocation -> {
            Media media = invocation.getArgument(0);
            media.setId(mediaId);
            return media;
        });
        when(mediaGateway.getMedia(anyInt())).thenAnswer(invocation -> {
            int id = invocation.getArgument(0);
            Media media = getMedia();
            media.setId(id);
            return media;
        });
    }
    
    @Test
    public void addMedia_CallsMediaGateway() {
        Media media = getMedia();
        
        Media returnedMedia = crudGateway.addMedia(media);
        
        verify(mediaGateway, times(1)).addMedia(media);
        assertEquals(mediaId, returnedMedia.getId());
    }
    
    @Test
    public void addMedia_CallsReviewGateway() {
        int numberOfReviews = 2;
        Media media = getMedia();
        media.getReviews().addAll(getList(numberOfReviews, this::getReview));
        
        Media returnedMedia = crudGateway.addMedia(media);
        
        verify(reviewGateway, times(numberOfReviews)).addReview(any());
        for (Review review : returnedMedia.getReviews())
            assertEquals(mediaId, review.getMediaId());
    }
    
    @Test
    public void addMedia_CallsFactiudGateway() {
        int numberOfFacts = 2;
        Media media = getMedia();
        media.getFactoids().addAll(getList(numberOfFacts, this::getFactoid));
        
        Media returnedMedia = crudGateway.addMedia(media);
        
        verify(factoidGateway, times(numberOfFacts)).addFactoid(any());
        for (Factoid factoid : returnedMedia.getFactoids())
            assertEquals(mediaId, factoid.getMediaId());
    }
    
    @Test
    public void getMedia_CallsMediaGateway() {
        Media returnedMedia = crudGateway.getMedia(mediaId);
        
        verify(mediaGateway).getMedia(mediaId);
        assertEquals(mediaId, returnedMedia.getId());
    }
    
    @Test
    public void getMedia_CallsReviewGateway() {
        int numberOfReviews = 2;
        List<Review> reviews = getList(numberOfReviews, this::getReview);
        when(reviewGateway.getReviews(mediaId)).thenReturn(reviews);
        
        Media returnedMedia = crudGateway.getMedia(mediaId);
        
        verify(reviewGateway).getReviews(mediaId);
        assertEquals(numberOfReviews, returnedMedia.getReviews().size());
    }
    
    @Test
    public void getMedia_CallsFactoidGateway() {
        int numberOfFacts = 2;
        List<Factoid> factoids = getList(numberOfFacts, this::getFactoid);
        when(factoidGateway.getFactoids(mediaId)).thenReturn(factoids);
        
        Media returnedMedia = crudGateway.getMedia(mediaId);
        
        verify(factoidGateway).getFactoids(mediaId);
        assertEquals(numberOfFacts, returnedMedia.getFactoids().size());
    }
    
    private Media getMedia() {
        Media media = new Media("TDD", "Uncle Bob", "Lecture");
        media.setEpisode("P1");
        return media;
    }
    
    private Review getReview() {
        Review review = new Review(5, LocalDate.now(), "Ben", "So-so");
        return review;
    }
    
    private Factoid getFactoid() {
        Factoid factoid = new Factoid("Genre", "Mystery");
        return factoid;
    }
    
    private <T> List<T> getList(int size, Supplier<T> supplier) {
        List<T> list = new ArrayList<>();
        for (int i = size; i > 0; i--)
            list.add(supplier.get());
        return list;
    }
}

package deleteme;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import retrospector.core.entity.Factoid;
import retrospector.core.entity.Media;
import retrospector.core.entity.Media.Type;
import retrospector.core.entity.Review;
import retrospector.hsqldb.datagateway.PropertyGateway;

/**
 *
 * @author nonfrt
 */
public class DataManager {
    static String connString = "jdbc:hsqldb:file:"+PropertyGateway.retroFolder;
    static Connection conn = null;
    static PropertyGateway PropertyManager;

    private static Connection getConnection(){
        return conn;
    }
    
    public static void makeBackup(String filename){
        try {
            String backupDir = PropertyGateway.retroFolder+"/Backup/";
            if (!filename.isEmpty()) {
                filename += ".tar.gz";
                backupDir += filename;
                Files.deleteIfExists(Paths.get(backupDir));
            }
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("BACKUP DATABASE TO '" + backupDir + "'");
        } catch(SQLException ex) {
            ex.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public static void makeBackup() {
        makeBackup("");
    }
    
    public static List<Media> getWishlist() {
        return getMedia(true);
    }
    
    public static List<Media> getMedia() {
        return getMedia(false);
    }
    
    /**
     * Gets media objects from the database
     * @param wishlistOrNo [true: wishlist only, false: media only]
     * @return 
     */
    private static List<Media> getMedia(boolean wishlistOrNo){
        Statement stmt;
        ResultSet rs = null;
        ResultSet rs2 = null;
        List<Media> list = new ArrayList<>();

        try{
            stmt = getConnection().createStatement();
            String equals = wishlistOrNo? "=" : "<>";
            rs = stmt.executeQuery("" +
                    "SELECT media.* \n" +
                    "FROM media \n" +
                    "LEFT JOIN review " +
                    "ON media.id = review.mediaID\n" +
                    "WHERE type"+equals+"'WISHLIST'\n" +
                    "GROUP BY media.id\n" +
                    "ORDER BY " + // Order so that no-reviews are first, then most recent reviews
                        "(CASE WHEN MAX(review.id) IS NULL THEN media.id ELSE 0 END) DESC, " +
                        "MAX(review.id) DESC " + 
                    "");
            while (rs.next()) {
                try{
                    Media medium = new Media();
                    medium.setId(rs.getInt("id"));
                    medium.setTitle(rs.getString("title"));
                    medium.setCreator(rs.getString("creator"));
                    medium.setSeason(rs.getString("season"));
                    medium.setEpisode(rs.getString("episode"));
                    medium.setDescription(rs.getString("description"));
                    medium.setCategory(rs.getString("category"));
                    medium.setType(Type.valueOf(rs.getString("type")));
                    try{
                        rs2 = stmt.executeQuery("select * from review where mediaID="+medium.getId());
                        while(rs2.next()){
                            try{
                                Review review = new Review();
                                review.setId(rs2.getInt("id"));
                                review.setMediaId(rs2.getInt("mediaID"));
                                review.setUser(rs2.getString("reviewer"));
                                review.setDate(rs2.getDate("date").toLocalDate());
                                review.setReview(rs2.getString("review"));
                                review.setRating(rs2.getInt("rate"));

                                medium.getReviews().add(review);
                            } catch(SQLException e){System.err.println("Get review failed. (getMedia)");}
                        }
                    } catch(SQLException e){System.err.println("Get review list failed. (getMedia)");}
                    try{
                        rs2 = stmt.executeQuery("select * from factoid where mediaID="+medium.getId());
                        while(rs2.next()){
                            try{
                                Factoid factoid = new Factoid();
                                factoid.setId(rs2.getInt("id"));
                                factoid.setMediaId(rs2.getInt("mediaID"));
                                factoid.setTitle(rs2.getString("title"));
                                factoid.setContent(rs2.getString("content"));

                                medium.getFactoids().add(factoid);
                            } catch(SQLException e){System.err.println("Get factoid failed. (getMedia)");}
                        }
                    } catch(SQLException e){System.err.println("Get factoid list failed. (getMedia)");}
                    list.add(medium);
                } catch(SQLException e){System.err.println("Get media failed. (getMedia)");}
            }
        } catch(SQLException e){System.err.println("Get media list failed. (getMedia)"); e.printStackTrace();}
        return  list;
    }
    
    public static List<Review> getReviews(){
        Statement stmt;
        ResultSet rs = null;

        List<Review> reviews = new ArrayList<>();
        try {
            stmt = getConnection().createStatement();       
            rs = stmt.executeQuery("select * from review");
            while (rs.next()) {
                try {
                    Review review = new Review();
                    review.setId(rs.getInt("id"));
                    review.setMediaId(rs.getInt("mediaID"));
                    review.setUser(rs.getString("reviewer"));
                    review.setDate(rs.getDate("date").toLocalDate());
                    review.setReview(rs.getString("review"));
                    review.setRating(rs.getInt("rate"));

                    reviews.add(review);
                } catch (SQLException e) {System.err.println("Get review failed. (getReviews)");}
            }
        } catch (SQLException e) {System.err.println("Get review list failed. (getReviews)");}
        return reviews;
    }
    
    public static List<Factoid> getFactoids(){
        Statement stmt;
        ResultSet rs = null;

        List<Factoid> factoids = new ArrayList<>();
        try {
            stmt = getConnection().createStatement();       
            rs = stmt.executeQuery("select * from factoid");
            while (rs.next()) {
                try {
                    Factoid factoid = new Factoid();
                    factoid.setId(rs.getInt("id"));
                    factoid.setMediaId(rs.getInt("mediaID"));
                    factoid.setTitle(rs.getString("title"));
                    factoid.setContent(rs.getString("content"));
                    factoids.add(factoid);
                } catch (SQLException e) {System.err.println("Get factoid failed. (getFactoids)");}
            }
        } catch (SQLException e) {System.err.println("Get factoid list failed. (getFactoids)");}
        return factoids;
    }
    
    public static List<Factoid> getFactoidsByType(String type){
        Statement stmt;
        ResultSet rs = null;

        List<Factoid> factoids = new ArrayList<>();
        try {
            stmt = getConnection().createStatement();       
            rs = stmt.executeQuery("select * from factoid where title='"+type+"'");
            while (rs.next()) {
                try {
                    Factoid factoid = new Factoid();
                    factoid.setId(rs.getInt("id"));
                    factoid.setMediaId(rs.getInt("mediaID"));
                    factoid.setTitle(rs.getString("title"));
                    factoid.setContent(rs.getString("content"));
                    factoids.add(factoid);
                } catch (SQLException e) {System.err.println("Get factoid failed. (getFactoids)");}
            }
        } catch (SQLException e) {System.err.println("Get factoid list failed. (getFactoids)");}
        return factoids;
    }
    
    /**
     * Create a list of all unique users in the db so far
     * @return 
     */
    public static List<String> getUsers(){
        Statement stmt;
        ResultSet rs = null;

        List<String> users = new ArrayList<>();
        try {
            stmt = getConnection().createStatement();       
            rs = stmt.executeQuery("select distinct reviewer from review");
            while (rs.next()) {
                try {
                    users.add(rs.getString(1));
                } catch (SQLException e) {System.err.println("Get reviewer failed. (getUsers)");}
            }
        } catch (SQLException e) {System.err.println("Get reviewer list failed. (getUsers)");}
        return users;
    }
    
    /**
     * Create a list of all unique titles in the db so far
     * @return 
     */
    public static List<String> getTitles(){
        Statement stmt;
        ResultSet rs = null;

        List<String> titles = new ArrayList<>();
        try {
            stmt = getConnection().createStatement();       
            rs = stmt.executeQuery("select distinct title from media m where m.type<>'WISHLIST'");
            while (rs.next()) {
                try {
                    titles.add(rs.getString(1));
                } catch (SQLException e) {System.err.println("Get title failed. (getTitles)");}
            }
        } catch (SQLException e) {System.err.println("Get title list failed. (getTitles)");}
        return titles;
    }
    
    /**
     * Create a list of all unique creator in the db so far
     * @return 
     */
    public static List<String> getCreators(){
        Statement stmt;
        ResultSet rs = null;

        List<String> creators = new ArrayList<>();
        try {
            stmt = getConnection().createStatement();       
            rs = stmt.executeQuery("select distinct creator from media m where m.type<>'WISHLIST'");
            while (rs.next()) {
                try {
                    creators.add(rs.getString(1));
                } catch (SQLException e) {System.err.println("Get creator failed. (getCreators)");}
            }
        } catch (SQLException e) {System.err.println("Get creator list failed. (getCreators)");}
        return creators;
    }
    
    /**
     * Create a list of all unique season in the db so far
     * @return 
     */
    public static List<String> getSeasons(){
        Statement stmt;
        ResultSet rs = null;

        List<String> seasons = new ArrayList<>();
        try {
            stmt = getConnection().createStatement();       
            rs = stmt.executeQuery("select distinct season from media m where m.type<>'WISHLIST'");
            while (rs.next()) {
                try {
                    seasons.add(rs.getString(1));
                } catch (SQLException e) {System.err.println("Get season failed. (getSeasons)");}
            }
        } catch (SQLException e) {System.err.println("Get season list failed. (getSeasons)");}
        return seasons;
    }
    
    /**
     * Create a list of all unique episode in the db so far
     * @return 
     */
    public static List<String> getEpisodes(){
        Statement stmt;
        ResultSet rs = null;

        List<String> episodes = new ArrayList<>();
        try {
            stmt = getConnection().createStatement();       
            rs = stmt.executeQuery("select distinct title from media m where m.type<>'WISHLIST'");
            while (rs.next()) {
                try {
                    episodes.add(rs.getString(1));
                } catch (SQLException e) {System.err.println("Get episode failed. (getEpisodes)");}
            }
        } catch (SQLException e) {System.err.println("Get episode list failed. (getEpisodes)");}
        return episodes;
    }

    /**
     * Create a list of all unique factoid contents in the db so far
     * @return 
     */
    public static List<String> getFactoidContents() {
        Statement stmt;
        ResultSet rs = null;

        List<String> contents = new ArrayList<>();
        try {
            stmt = getConnection().createStatement();
            rs = stmt.executeQuery("select distinct content from factoid f");
            while (rs.next()) {
                try {
                    contents.add(rs.getString(1));
                } catch (SQLException e) {
                    System.err.println("Get factoid failed. (getFactoidContents)");
                }
            }
        } catch (SQLException e) {
            System.err.println("Get factoid list failed. (getFactoidContents)");
        }
        return contents;
    }
    
    /**
     * Create a new media in the DB
     * @param media 
     */
    public static int createDB(Media media){
        int id = -1;
        try {
            PreparedStatement pstmt;

            pstmt = getConnection().prepareStatement(
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
            if(updated==1 && key){
                id = rs.getInt(1);
                for (Review review : media.getReviews()) {
                    review.setMediaId(id);
                    review.setId(createDB(review));
                }
                for (Factoid factoid : media.getFactoids()) {
                    factoid.setMediaId(id);
                    factoid.setId(createDB(factoid));
                }
            } else if(media.getFactoids().size()>0 || media.getReviews().size()>0) {
                System.err.println("Reviews/Factoids not Saved! :(");
            }
            
        } catch (SQLException ex) {
            System.err.println("createDB error in connection" + ex);
        }
        return id;
    }
    
    /**
     * Create a new review in the DB
     * @param review 
     */
    public static int createDB(Review review){
        int id = -1;
        try {
            PreparedStatement pstmt;

            pstmt = getConnection().prepareStatement(
                    "insert into review(mediaId,reviewer,date,review,rate) values(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, review.getMediaId());
            pstmt.setString(2, review.getUser());
            pstmt.setDate(3, Date.valueOf(review.getDate()));
            pstmt.setString(4, review.getReview());
            pstmt.setInt(5, review.getRating()); // If ratings ever get decimal again fix this!
            int updated = pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            boolean key = rs.next();
            if(updated==1 && key){
                id = rs.getInt(1);
            }
            
            
        } catch (SQLException ex) {
            System.err.println("createDB error in connection" + ex);
        }
        return id;
    }
    
    /**
     * Create a new factoid in the DB
     * @param factoid 
     */
    public static int createDB(Factoid factoid){
        int id = -1;
        try {
            PreparedStatement pstmt;

            pstmt = getConnection().prepareStatement(
                    "insert into factoid(mediaId,title,content) values(?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, factoid.getMediaId());
            pstmt.setString(2, factoid.getTitle());
            pstmt.setString(3, factoid.getContent());
            int updated = pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            boolean key = rs.next();
            if(updated==1 && key){
                id = rs.getInt(1);
            }
            
            
        } catch (SQLException ex) {
            System.err.println("createDB error in connection" + ex);
        }
        return id;
    }
    
    /**
     * Update an existing media in DB
     * @param media 
     */
    public static void updateDB(Media media){
        try {
            PreparedStatement pstmt;

            pstmt = getConnection().prepareStatement("update media set title=?,creator=?,season=?,episode=?,description=?,category=?,type=? where id=?");
            pstmt.setString(1, media.getTitle());
            pstmt.setString(2, media.getCreator());
            pstmt.setString(3, media.getSeason());
            pstmt.setString(4, media.getEpisode());
            pstmt.setString(5, media.getDescription());
            pstmt.setString(6, media.getCategory());                  
            pstmt.setString(7, media.getType().toString());                  
            pstmt.setInt(8, media.getId());                  
            pstmt.executeUpdate();
            
            for (Review review : media.getReviews()) {
                review.setMediaId(media.getId());
                if (review.getId() != null && review.getId() != 0)
                    updateDB(review);
                else
                    createDB(review);
            }
            
            for (Factoid factoid : media.getFactoids()) {
                factoid.setMediaId(media.getId());
                if (factoid.getId() != null && factoid.getId() != 0)
                    updateDB(factoid);
                else
                    createDB(factoid);
            }
            
            if(media.getId()<1)
                System.err.println("Bad media update");
        } catch (SQLException ex) {
            System.err.println("updateDB error in connection" + ex);
            ex.printStackTrace();
        }
    }
    
    /**
     * Update an existing media in DB
     * @param review 
     */
    public static void updateDB(Review review){
        try {
            PreparedStatement pstmt;

            pstmt = getConnection().prepareStatement("update review set mediaId=?,reviewer=?,date=?,review=?,rate=? where id=?");
            pstmt.setInt(1, review.getMediaId());
            pstmt.setString(2, review.getUser());
            pstmt.setDate(3, Date.valueOf(review.getDate()));
            pstmt.setString(4, review.getReview());
            pstmt.setInt(5, review.getRating()); // If ratings ever get decimal again fix this!
            pstmt.setInt(6, review.getId()); 
            pstmt.executeUpdate();
            
            if(review.getId()<1)
                System.err.println("Bad review update");
        } catch (SQLException ex) {
            System.err.println("in connection" + ex);
        }
    }
    
    /**
     * Update an existing factoid in DB
     * @param factoid 
     */
    public static void updateDB(Factoid factoid){
        try {
            PreparedStatement pstmt;

            pstmt = getConnection().prepareStatement("update factoid set mediaId=?, title=?, content=? where id=?");
            pstmt.setInt(1, factoid.getMediaId());
            pstmt.setString(2, factoid.getTitle());
            pstmt.setString(3, factoid.getContent());
            pstmt.setInt(4, factoid.getId()); 
            pstmt.executeUpdate();
            
            if(factoid.getId()<1)
                System.err.println("Bad factoid update");
        } catch (SQLException ex) {
            System.err.println("in connection" + ex);
        }
    }
    
    /**
     * Delete media from DB
     * @param media 
     */
    public static void deleteDB(Media media){
        try {
            PreparedStatement pstmt;

            pstmt = getConnection().prepareStatement("delete from media where id=?");
            pstmt.setInt(1, media.getId()); 
            pstmt.executeUpdate();

            pstmt = getConnection().prepareStatement("delete from review where mediaId=?");
            pstmt.setInt(1, media.getId()); 
            pstmt.executeUpdate();

            pstmt = getConnection().prepareStatement("delete from media where mediaId=?");
            pstmt.setInt(1, media.getId()); 
            pstmt.executeUpdate();
            
        } catch (SQLException ex) {
            System.err.println("(deleteMedia) in connection" + ex);
        }
    }
    
    /**
     * Delete review from DB
     * @param review 
     */
    public static void deleteDB(Review review){
        try {
            PreparedStatement pstmt;

            pstmt = getConnection().prepareStatement("delete from review where id=?");
            pstmt.setInt(1, review.getId()); 
            pstmt.executeUpdate();
            
        } catch (SQLException ex) {
            System.err.println("(deleteReview) in connection" + ex);
        }
    }
    
    /**
     * Delete factoid from DB
     * @param factoid 
     */
    public static void deleteDB(Factoid factoid){
        try {
            PreparedStatement pstmt;

            pstmt = getConnection().prepareStatement("delete from factoid where id=?");
            pstmt.setInt(1, factoid.getId()); 
            pstmt.executeUpdate();
            
        } catch (SQLException ex) {
            System.err.println("(deleteFactoid) in connection" + ex);
        }
    }
    
    /**
     * Gets the media from the observable list
     * @param id
     * @return 
     */
    public static Media getMedia(int id){
        Statement stmt;
        ResultSet rs = null;
        ResultSet rs2 = null;
        Media medium = new Media();

        try {
            stmt = getConnection().createStatement();       
            rs = stmt.executeQuery("select * from media where id="+id);
            rs.next();
            try{
                medium.setId(rs.getInt("id"));
                medium.setTitle(rs.getString("title"));
                medium.setCreator(rs.getString("creator"));
                medium.setSeason(rs.getString("season"));
                medium.setEpisode(rs.getString("episode"));
                medium.setDescription(rs.getString("description"));
                medium.setCategory(rs.getString("category"));
                medium.setType(Type.valueOf(rs.getString("type")));
                try{
                    rs2 = stmt.executeQuery("select * from review where mediaID="+medium.getId());
                    while(rs2.next()){
                        try{
                            Review review = new Review();
                            review.setId(rs2.getInt("id"));
                            review.setMediaId(rs2.getInt("mediaID"));
                            review.setUser(rs2.getString("reviewer"));
                            review.setDate(rs2.getDate("date").toLocalDate());
                            review.setReview(rs2.getString("review"));
                            review.setRating(rs2.getInt("rate"));

                            medium.getReviews().add(review);
                        } catch(SQLException e){System.err.println("Get review failed. (getMedia)");}
                    }
                } catch(SQLException e){System.err.println("Get review list failed. (getMedia)");}
                try{
                    rs2 = stmt.executeQuery("select * from factoid where mediaID="+medium.getId());
                    while(rs2.next()){
                        try{
                            Factoid factoid = new Factoid();
                            factoid.setId(rs2.getInt("id"));
                            factoid.setMediaId(rs2.getInt("mediaID"));
                            factoid.setTitle(rs2.getString("title"));
                            factoid.setContent(rs2.getString("content"));

                            medium.getFactoids().add(factoid);
                        } catch(SQLException e){System.err.println("Get factoid failed. (getMedia)");}
                    }
                } catch(SQLException e){System.err.println("Get factoid list failed. (getMedia)");}
            } catch(SQLException e){System.err.println("Get media failed. (getMedia)");}
        } catch (SQLException e) {System.err.println("Get media list failed. (getMedia)");}
        
        return medium;
    }
    
    /**
     * @param id
     * @return 
     */
    public static Review getReview(int id){
        Statement stmt;
        ResultSet rs = null;
        Review review = new Review();
        try {
            stmt = getConnection().createStatement();       
            rs = stmt.executeQuery("select * from review where id="+id);
            rs.next();
            try {
                review.setId(rs.getInt("id"));
                review.setMediaId(rs.getInt("mediaID"));
                review.setUser(rs.getString("reviewer"));
                review.setDate(rs.getDate("date").toLocalDate());
                review.setReview(rs.getString("review"));
                review.setRating(rs.getInt("rate"));

            } catch (SQLException e) {System.err.println("Set review failed. (getReviews)");}
        } catch (SQLException e) {System.err.println("Get review from db failed. (getReviews)");}
        return review;
    }
    
    /**
     * @param id
     * @return 
     */
    public static Factoid getFactoid(int id){
        Statement stmt;
        ResultSet rs = null;
        Factoid factoid = new Factoid();
        try {
            stmt = getConnection().createStatement();       
            rs = stmt.executeQuery("select * from factoid where id="+id);
            rs.next();
            try {
                factoid.setId(rs.getInt("id"));
                factoid.setMediaId(rs.getInt("mediaID"));
                factoid.setTitle(rs.getString("title"));
                factoid.setContent(rs.getString("content"));

            } catch (SQLException e) {System.err.println("Set factoid failed. (getFactoids)");}
        } catch (SQLException e) {System.err.println("Get factoid from db failed. (getFactoids)");}
        return factoid;
    }
}

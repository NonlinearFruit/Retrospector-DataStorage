
package retrospector.hsqldb.datagateway;

public interface PropertyGateway {
    public static final String retroFolder = System.getProperty("user.home")+"/Retrospector";
    public static final String configPath = retroFolder+"/Retrospector.config";
    public static final String pluginPath = retroFolder+"/Plugins";
    public static final String backupPath = retroFolder+"/Backup";
    public static final String connectionString = "jdbc:hsqldb:file:"+PropertyGateway.retroFolder;
    
    public String getDefaultUser();
    
    public Integer getMaxRating();
    
    public Integer getDefaultRating();
    
    public String[] getCategories();
    
    public String[] getFactoidTypes();
    
    public String getGithubUser();
    
    public Integer getPastDays();
}

package retrospector.hsqldb.datagateway;

import org.apache.commons.dbutils.ResultSetHandler;

public interface DataConnector {
  public void exit();
  public void execute(String sql, Object... parameters);
  public <T> T insert(ResultSetHandler<T> handler, String query, Object... parameters);
  public <T> T select(ResultSetHandler<T> handler, String query, Object... parameters);
}

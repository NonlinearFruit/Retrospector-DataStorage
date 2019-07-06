package retrospector.hsqldb.fakes;

import org.apache.commons.dbutils.ResultSetHandler;
import retrospector.hsqldb.datagateway.DataConnector;

public class DataConnector_DefaultResults implements DataConnector {
  @Override
  public void exit() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public <T> T insert(ResultSetHandler<T> handler, String query, Object... parameters) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public <T> T select(ResultSetHandler<T> handler, String query, Object... parameters) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void execute(String sql, Object... parameters) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}

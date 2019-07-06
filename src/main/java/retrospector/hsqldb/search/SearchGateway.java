package retrospector.hsqldb.search;

import java.util.List;
import java.util.Map;
import retrospector.core.datagateway.SearchDataGateway;
import retrospector.core.interactor.search.QueryTree;
import retrospector.core.interactor.search.RetrospectorAttribute;

public class SearchGateway implements SearchDataGateway {

  @Override
  public List<Map<RetrospectorAttribute, String>> search(QueryTree qt) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
}

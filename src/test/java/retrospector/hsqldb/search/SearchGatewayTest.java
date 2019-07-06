package retrospector.hsqldb.search;

import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import retrospector.core.interactor.search.QueryTree;
import retrospector.core.interactor.search.RetrospectorAttribute;

public class SearchGatewayTest {

  @Before
  public void setUp() {
  }

  @Test
  public void testSearch() {
    System.out.println("search");
    QueryTree qt = null;
    SearchGateway instance = new SearchGateway();
    List<Map<RetrospectorAttribute, String>> expResult = null;
    List<Map<RetrospectorAttribute, String>> result = instance.search(qt);
    assertEquals(expResult, result);
    fail("The test case is a prototype.");
  }
}
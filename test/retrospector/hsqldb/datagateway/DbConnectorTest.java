
package retrospector.hsqldb.datagateway;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import retrospector.hsqldb.exceptions.DatabaseConnectionFailedException;
import retrospector.hsqldb.exceptions.QueryFailedException;

public class DbConnectorTest {
    
    public static final String testConnectionString = "jdbc:hsqldb:file:"+System.getProperty("user.home")+"/Downloads/";
    public static final String testDatabaseLockFile = System.getProperty("user.home")+"/Downloads/.lck";
    
    public static void deleteLockFile(){
        try {
        Files.deleteIfExists(Paths.get(testDatabaseLockFile));
        } catch (IOException ex) {
            fail("Failed to delete lock file");
        }
    }
    
    public static void clearDatabase(DbConnector connector) {
        connector.execute("DROP SCHEMA PUBLIC CASCADE");
    }
    
    private DbConnector connector;
    
    @Before
    public void setUp() {
        deleteLockFile();
    }
    
    @After
    public void tearDown() {
        if (connector != null)
            connector.exit();
    }

    @Test
    public void constructor_LocksDatabase() {
        connector = new DbConnector(testConnectionString);
        
        assertTrue(Files.exists(Paths.get(testDatabaseLockFile)));
    }
    
    @Test(expected = DatabaseConnectionFailedException.class)
    public void constructor_ThrowsException_WhenInvalidConnectionString() {
        connector = new DbConnector("");
    }
      
    @Test
    public void exit_FreesDatabase(){
        connector = new DbConnector(testConnectionString);
        
        connector.exit();
        
        assertFalse(Files.exists(Paths.get(testDatabaseLockFile)));
    }
    
    @Test
    public void execute_ExecutesSql() {
        connector = new DbConnector(testConnectionString);
        clearDatabase(connector);
        
        setupDatabase();
        
        connector.exit();
        connector = new DbConnector(testConnectionString);
        try{
            setupDatabase();
            fail("Tables not created");
        } catch(QueryFailedException ex) {}
    }
    
    @Test(expected = QueryFailedException.class)
    public void execute_ThrowsException_WhenInvalidSql() {
        connector = new DbConnector(testConnectionString);
        
        connector.execute("afefges einsfneskfs fsefksnflns");
    }
    
    @Test
    public void insert_InsertsRecord() {
        connector = new DbConnector(testConnectionString);
        clearDatabase(connector);
        setupDatabase();
        ResultSetHandler<Integer> handler=new ScalarHandler<>();
        
        int firstId = connector.insert(handler, "INSERT INTO PUBLIC.EMPLOYEE (firstname, lastname, salary) VALUES ('name', 'last', 1.2)");
        int secondId = connector.insert(handler, "INSERT INTO PUBLIC.EMPLOYEE (firstname, lastname, salary) VALUES ('name', 'last', 1.2)");
        int thirdId = connector.insert(handler, "INSERT INTO PUBLIC.EMPLOYEE (firstname, lastname, salary) VALUES ('name', 'last', 1.2)");
        
        assertTrue(1 == firstId);
        assertTrue(2== secondId);
        assertTrue(3 == thirdId);
    }
    
    @Test(expected = QueryFailedException.class)
    public void insert_ThrowsException_WhenInvalidSql() {
        connector = new DbConnector(testConnectionString);
        
        connector.insert(new BeanListHandler<>(String.class), "a daw dawdad");
    }
    
    @Test
    public void select_ReturnsRecord() {
        connector = new DbConnector(testConnectionString);
        clearDatabase(connector);
        setupDatabase();
        int id = addEmployee();
        ResultSetHandler<Employee> handler = new ResultSetHandler<Employee>(){
            @Override
            public Employee handle(ResultSet rs) throws SQLException {
                rs.next();
                Employee employee = new Employee();
                employee.firstname = rs.getString("firstname");
                employee.lastname = rs.getString("lastname");
                employee.salary = rs.getDouble("salary");
                return employee;
            }
            
        };
        
        Employee employee = connector.select(handler, "Select * from employee where id="+id);
        
        assertNotEquals(null, employee.firstname);
        assertNotEquals(null, employee.lastname);
        assertNotEquals(null, employee.salary);
    }
    
    @Test
    public void select_ReturnsRecords() {
        connector = new DbConnector(testConnectionString);
        clearDatabase(connector);
        setupDatabase();
        int id1 = addEmployee();
        int id2 = addEmployee();
        int id3 = addEmployee();
        ResultSetHandler<List<Employee>> handler = new ResultSetHandler<List<Employee>>(){
            @Override
            public List<Employee> handle(ResultSet rs) throws SQLException {
                List<Employee> list = new ArrayList<>();
                
                while (rs.next()) {
                    Employee employee = new Employee();
                    employee.firstname = rs.getString("firstname");
                    employee.lastname = rs.getString("lastname");
                    employee.salary = rs.getDouble("salary");
                    list.add(employee);
                }
                
                return list;
            }
            
        };
        
        List<Employee> employees = connector.select(handler, "Select * from employee");
        
        assertNotEquals(null, employees);
        assertNotEquals(0, employees.size());
        for (Employee employee : employees) {
            assertNotEquals(null, employee.firstname);
            assertNotEquals(null, employee.lastname);
            assertNotEquals(null, employee.salary);
        }
    }
    
    @Test(expected = QueryFailedException.class)
    public void select_ThrowsException_WhenInvalidSql() {
        connector = new DbConnector(testConnectionString);
        
        connector.select(new BeanListHandler<>(String.class), "wdaw daw da dwa d");
    }
    
    private void setupDatabase() {
        connector.execute("CREATE TABLE employee(" +
            "    id integer not null PRIMARY KEY generated always as identity (start with 1, increment by 1)," +
            "    firstname varchar(255)," +
            "    lastname varchar(255)," +
            "    salary double)"
            );
        connector.execute("CREATE TABLE email(\n" +
            "    id integer not null PRIMARY KEY generated always as identity (start with 1, increment by 1),\n" +
            "    employeeid int,\n" +
            "    address varchar(255))"
            );
    }
    
    private int addEmployee() {
        return connector.insert(new ScalarHandler<Integer>(), "INSERT INTO PUBLIC.EMPLOYEE (firstname, lastname, salary) VALUES ('name', 'last', 1.2)");
    }
    
    public class Employee{
        private String firstname;
        private String lastname;
        private Double salary;

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public Double getSalary() {
            return salary;
        }

        public void setSalary(Double salary) {
            this.salary = salary;
        }
    }
}

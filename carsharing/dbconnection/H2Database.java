package carsharing.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2Database {

  private final String dbPath = "jdbc:h2:C:\\Users\\Sasha\\IdeaProjects\\Car Sharing\\Car Sharing\\task\\src\\carsharing\\db\\";
  private String URL;

  public H2Database(String dbFileName) {
    URL = dbPath + dbFileName.trim();
    init();
  }

  public void setUrl(String dbFileName) {
    URL = dbPath + dbFileName.trim();
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL);
  }

  private void init() {
    final String createCompanyTableQuery = "CREATE TABLE COMPANY("
        + "ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, "
        + "NAME VARCHAR(50) NOT NULL UNIQUE);";
    final String createCarTableQuery = "CREATE TABLE CAR("
        + "ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,"
        + "NAME VARCHAR(50) NOT NULL UNIQUE,"
        + "COMPANY_ID INTEGER NOT NULL,"
        + "FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID));";
    try (Connection conn = getConnection()) {
      Statement statement = conn.createStatement();
      statement.execute(createCompanyTableQuery);
      statement.execute(createCarTableQuery);
    } catch (SQLException e) {
      //e.printStackTrace();
    }
  }

}

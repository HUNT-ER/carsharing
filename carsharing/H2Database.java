package carsharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2Database {
  private final String dbPath = "jdbc:h2:C:\\Users\\Sasha\\IdeaProjects\\Car Sharing\\Car Sharing\\task\\src\\carsharing\\db\\";
  private String URL;

  public H2Database(String dbFileName) {
    URL = dbPath + dbFileName.trim();
    System.out.println(URL);
    init();
  }

  public void setUrl(String dbFileName) {
    URL = dbPath + dbFileName.trim();
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL);
  }

  private void init() {
    try (Connection conn = getConnection()) {
      Statement statement = conn.createStatement();
      statement.execute("create table company(id integer, name varchar(100))");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}

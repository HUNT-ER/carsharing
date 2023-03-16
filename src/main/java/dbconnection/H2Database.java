package dbconnection;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class H2Database {

  private final String DB_URL = "jdbc:h2:C:\\Users\\Sasha\\IdeaProjects\\carsharing\\src\\main\\resources\\";
  private final String FILE_PATH = "C:\\Users\\Sasha\\IdeaProjects\\carsharing\\src\\main\\resources\\";
  private final String DEFAULT_DB = "cars";
  private String dbFullUrl;
  private String dbFileName;

  public H2Database(String dbFileName) {
    this.dbFileName = dbFileName;
    dbFullUrl = DB_URL + dbFileName.trim();
    if (!isExistDbFile(FILE_PATH + dbFileName.trim())) {
      dbFullUrl = DB_URL + DEFAULT_DB;
    }
  }

  public H2Database(Path path) {
  }

  public H2Database() {
    dbFullUrl = DB_URL + DEFAULT_DB;
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(dbFullUrl);
  }

  public String getDbFileName() {
    if (dbFileName == null) {
      return DEFAULT_DB;
    }
    return dbFileName;
  }

  private boolean isExistDbFile(String filePath) {
    return new File(filePath + ".mv.db").exists();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof H2Database)) {
      return false;
    }
    H2Database that = (H2Database) o;
    return dbFullUrl.equals(that.dbFullUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dbFullUrl);
  }
}

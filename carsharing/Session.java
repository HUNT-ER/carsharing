package carsharing;

public class Session {
  private H2Database dbConnection;

  public Session(String dbFileName) {
    dbConnection = new H2Database(dbFileName);
  }
}

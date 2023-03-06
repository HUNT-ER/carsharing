package carsharing.program;

import carsharing.dbconnection.H2Database;
import carsharing.ui.LogInUIService;
import carsharing.ui.UIService;
import carsharing.user.User;

public class Session {

  private H2Database dbConnection;
  private UIService menu;
  private User user;
  private boolean isClosed;

  public Session(String dbFileName) {
    dbConnection = new H2Database(dbFileName);
    isClosed = false;
    menu = new LogInUIService(this);
  }

  public void setMenu(UIService menu) {
    this.menu = menu;
  }

  public void close() {
    isClosed = true;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public H2Database getDbConnection() {
    return dbConnection;
  }

  public void start() {
    while (!isClosed) {
      menu.showUI();
    }
  }
}

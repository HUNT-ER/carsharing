package program;

import dbconnection.H2Database;
import ui.LogInUIService;
import ui.UIService;

public class Session {

  private H2Database dbConnection;
  private UIService menu;
  private boolean isClosed;

  public Session(String dbFileName) {
    dbConnection = new H2Database(dbFileName);
    isClosed = false;
    menu = new LogInUIService(this);
  }

  public Session() {
    dbConnection = new H2Database();
    isClosed = false;
    menu = new LogInUIService(this);
  }

  public void setMenu(UIService menu) {
    this.menu = menu;
  }

  public void close() {
    isClosed = true;
  }

  public H2Database getDbConnection() {
    return dbConnection;
  }

  public void start() {
    while (!isClosed) {
      menu.showUI();
    }
  }

  public boolean isClosed() {
    return isClosed;
  }

  public UIService getMenu() {
    return menu;
  }
}

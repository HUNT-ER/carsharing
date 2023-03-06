package carsharing.ui;

import carsharing.program.Session;

public class LogInUIService extends UIService {

  public LogInUIService(Session session) {
    super(session);
  }

  @Override
  public void showMenu() {
    System.out.println("\n1. Log in as a manager\n"
        + "0. Exit");
  }

  @Override
  public void chooseMenu(int choice) {
    switch (choice) {
      case 1:
        session.setMenu(new MainUIService(session));
        break;
      case 0:
        session.close();
        break;
    }
  }
}
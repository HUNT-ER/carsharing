package carsharing.ui;

import carsharing.input.UserInputService;
import carsharing.program.Session;

public abstract class UIService {
  protected Session session;

  public UIService(Session session) {
    this.session = session;
  }
  public void showUI() {
      showMenu();
      chooseMenu(UserInputService.getIntInput());
  }

  public abstract void showMenu();

  public abstract void chooseMenu(int choice);

}

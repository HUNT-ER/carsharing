package ui;

import entities.Car;
import program.Session;

public class CarUIService extends UIService{

  Car car;

  public CarUIService(Session session, Car car) {
    super(session);
    this.car = car;
  }

  @Override
  public void showMenu() {

  }

  @Override
  public void chooseMenu(int choice) {

  }
}

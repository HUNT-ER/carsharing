package carsharing.ui;

import carsharing.dbconnection.CarDao;
import carsharing.entities.Car;
import carsharing.entities.Company;
import carsharing.entities.NewCar;
import carsharing.exceptions.CarExistsException;
import carsharing.input.UserInputService;
import carsharing.program.Session;
import java.util.Comparator;
import java.util.Map;

public class CompanyUIService extends UIService {

  private Company company;

  public CompanyUIService(Session session, Company company) {
    super(session);
    this.company = company;
  }

  @Override
  public void showMenu() {
    System.out.println("\n'" + company + "' company\n"
        + "1. Car list\n"
        + "2. Create a car\n"
        + "0. Back");
  }

  @Override
  public void chooseMenu(int choice) {
    switch (choice) {
      case 1:
        chooseCar();
        break;
      case 2:
        createCar();
        break;
      case 0:
        //возвращаемся назад на уровень логина
        session.setMenu(new ManagerUIService(session));
        break;
    }
  }

  private void chooseCar() {

    Map<Integer, Car> menuChoice = getMenu(new CarDao(session.getDbConnection()),
        car -> car.getCompany().getId() == company.getId(), Comparator.comparing(Car::getId));

    printMenu("Car list:", menuChoice);

  }

  private void createCar() {
    System.out.println("\nEnter the car name:");
    CarDao carDao = new CarDao(session.getDbConnection());
    try {
      carDao.save(new NewCar(UserInputService.getStringInput(), company));
      System.out.println("The car was added!");
    } catch (CarExistsException e) {
      System.out.println("\nSuch car already exists.");
    }
  }

  @Override
  protected void printIfEmpty() {
    System.out.println("\nThe car list is empty!");
  }
}

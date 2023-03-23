package ui;

import dbconnection.CarDao;
import entities.Car;
import entities.Company;
import entities.NewCar;
import exceptions.CarExistsException;
import input.UserInputService;
import java.util.Comparator;
import java.util.Map;
import program.Session;

public class CompanyUIService extends UIService {

  private Company company;

  public Company getCompany() {
    return company;
  }

  public CompanyUIService(Session session, Company company) {
    super(session);
    this.company = company;
  }

  @Override
  public String showMenu() {
    String menu = "\n'" + company + "' company\n"
        + "1. Car list\n"
        + "2. Create a car\n"
        + "0. Back\n";
    System.out.println(menu);
    return menu;
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
      carDao.save(new NewCar(UserInputService.getStringInput(scanner), company));
      System.out.println("The car was added!");
    } catch (CarExistsException e) {
      System.out.println("\nSuch car already exists.");
    }
  }

  @Override
  protected String printIfEmpty() {
    String output = "\nThe car list is empty!";
    System.out.println(output);
    return output;
  }
}

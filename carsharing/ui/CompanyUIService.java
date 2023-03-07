package carsharing.ui;

import carsharing.dbconnection.CarDao;
import carsharing.entities.Car;
import carsharing.entities.Company;
import carsharing.entities.NewCar;
import carsharing.exceptions.CarExistsException;
import carsharing.input.UserInputService;
import carsharing.program.Session;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        session.setMenu(new MainUIService(session));
        break;
    }
  }

  private void chooseCar() {
    //отбираем машины принадлежащие конкретной компании
    List<Car> cars = new CarDao(session.getDbConnection()).getAll()
        .stream().filter(car -> car.getCompany().getId() == company.getId())
        .collect(Collectors.toList());

    //сортируем в естественном порядке по id
    Collections.sort(cars, Comparator.comparing(Car::getId));

    //заполняем список компаний
    Map<Integer, Car> menuChoice = IntStream.range(1, cars.size() + 1)
        .boxed().collect(Collectors.toMap(i -> i, i -> cars.get(i - 1)));

    //формируем финальный вывод
    if (menuChoice.size() != 0) {
      //распечатываем полностью все машины
      System.out.println("\nCar list:");
      menuChoice.forEach((k, v) -> System.out.println(k + ". " + v));
    } else {
      System.out.println("\nThe car list is empty!");
    }

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
}

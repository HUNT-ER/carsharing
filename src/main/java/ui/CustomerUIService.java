package ui;

import dbconnection.CarDao;
import dbconnection.CompanyDao;
import dbconnection.CustomerDao;
import entities.Car;
import entities.Company;
import entities.Customer;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import program.Session;

public class CustomerUIService extends UIService {

  private Customer customer;

  public CustomerUIService(Session session, Customer customer) {
    super(session);
    this.customer = customer;
  }

  public Customer getCustomer() {
    return customer;
  }

  @Override
  public String showMenu() {
    String menu = "\n1. Rent a car\n"
        + "2. Return a rented car\n"
        + "3. My rented car\n"
        + "0. Back\n";
    System.out.println(menu);
    return menu;
  }

  @Override
  public void chooseMenu(int choice) {
    switch (choice) {
      case 1:
        rentCar();
        break;
      case 2:
        returnRentedCar();
        break;
      case 3:
        showRentedCar();
        break;
      case 0:
        //возвращаемся назад на уровень логина
        session.setMenu(new LogInUIService(session));
        break;
    }
  }

  private void rentCar() {
    if (customer.hasActiveRent()) {
      System.out.println("\nYou've already rented a car!");
      return;
    }

    Map<Integer, Company> companies = getMenu(new CompanyDao(session.getDbConnection()),
        company -> true, Comparator.comparing(Company::getId));

    //выводим выбор компании
    Consumer<Integer> companyAction = compChoice -> {
      Company company = companies.get(compChoice);

      Map<Integer, Car> cars = getMenu(new CarDao(session.getDbConnection()),
          car -> car.getCompany().getId() == company.getId() & car.isRented() == false,
          Comparator.comparing(Car::getId));
      //после выбора компании переходим к выбору автомобиля
      printMenuAndMakeChoice("Choose a car:", cars, carChoice -> {
        //присваиваем машину клиенту, сохраняем в базу данных
        Car car = cars.get(carChoice);
        customer.setCar(car);
        new CustomerDao(session.getDbConnection()).update(customer);
        System.out.println("\nYou rented '" + car + "'");
      });

    };

    //печатаем меню возможных компаний
    printMenuAndMakeChoice("Choose a company:", companies, companyAction);
  }

  private void returnRentedCar() {
    if (!customer.hasActiveRent()) {
      System.out.println("\nYou didn't rent a car!");
      return;
    }
    customer.setCar(null);
    new CustomerDao(session.getDbConnection()).update(customer);
    System.out.println("\nYou've returned a rented car!");
  }

  private void showRentedCar() {
    if (!customer.hasActiveRent()) {
      System.out.println("\nYou didn't rent a car!");
      return;
    }
    StringBuilder output = new StringBuilder("\n");
    output.append("You rented car:\n");
    output.append(customer.getCar() + "\n");
    output.append("Company:\n");
    output.append(customer.getCar().getCompany());
    System.out.println(output);
  }
}

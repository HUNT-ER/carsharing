package ui;

import dbconnection.CustomerDao;
import entities.Customer;
import entities.NewCustomer;
import exceptions.CustomerExistsException;
import input.UserInputService;
import java.util.Comparator;
import java.util.Map;
import program.Session;

public class LogInUIService extends UIService {

  public LogInUIService(Session session) {
    super(session);
  }

  @Override
  public void showMenu() {
    System.out.println("\n1. Log in as a manager\n"
        + "2. Log in as a customer\n"
        + "3. Create a customer\n"
        + "0. Exit");
  }

  @Override
  public void chooseMenu(int choice) {
    switch (choice) {
      case 1:
        session.setMenu(new ManagerUIService(session));
        break;
      case 2:
        chooseCustomer();
        break;
      case 3:
        createCustomer();
        break;
      case 0:
        session.close();
        break;
    }
  }

  private void chooseCustomer() {
    Map<Integer, Customer> menuChoice = getMenu(new CustomerDao(session.getDbConnection()),
        (customer) -> true, Comparator.comparing(Customer::getId));

    printMenuAndMakeChoice("Customer list:", menuChoice,
        (input) -> session.setMenu(new CustomerUIService(session, menuChoice.get(input))));
  }

  private void createCustomer() {
    System.out.println("\nEnter the customer name:");
    CustomerDao customerDao = new CustomerDao(session.getDbConnection());
    try {
      customerDao.save(new NewCustomer(UserInputService.getStringInput()));
      System.out.println("The customer was added!");
    } catch (CustomerExistsException e) {
      System.out.println("\nSuch customer already exists.");
    }
  }

  @Override
  protected void printIfEmpty() {
    System.out.println("\nThe customer list is empty!");
  }
}
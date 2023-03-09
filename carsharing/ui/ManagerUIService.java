package carsharing.ui;

import carsharing.dbconnection.CompanyDao;
import carsharing.entities.Company;
import carsharing.entities.NewCompany;
import carsharing.exceptions.CompanyExistsException;
import carsharing.input.UserInputService;
import carsharing.program.Session;
import java.util.Comparator;
import java.util.Map;

public class ManagerUIService extends UIService {

  public ManagerUIService(Session session) {
    super(session);
  }

  @Override
  public void showMenu() {
    System.out.println("\n1. Company list\n"
        + "2. Create a company\n"
        + "0. Back");
  }

  @Override
  public void chooseMenu(int choice) {
    switch (choice) {
      case 1:
        chooseCompany();
        break;
      case 2:
        createCompany();
        break;
      case 0:
        //возвращаемся назад на уровень логина
        session.setMenu(new LogInUIService(session));
        break;
    }
  }

  private void chooseCompany() {

    Map<Integer, Company> menuChoice = getMenu(new CompanyDao(session.getDbConnection()),
        (company -> true), Comparator.comparing(Company::getId));

    printMenuAndMakeChoice("Choose the company:", menuChoice,
        (input) -> session.setMenu(new CompanyUIService(session, menuChoice.get(input))));
  }

  private void createCompany() {
    System.out.println("Enter the company name:");
    CompanyDao companyDao = new CompanyDao(session.getDbConnection());
    try {
      companyDao.save(new NewCompany(UserInputService.getStringInput()));
      System.out.println("The company was created!");
    } catch (CompanyExistsException e) {
      System.out.println("\nSuch company already exists.");
    }
  }

  @Override
  protected void printIfEmpty() {
    System.out.println("\nThe company list is empty");
  }
}

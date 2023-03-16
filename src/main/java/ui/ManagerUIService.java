package ui;

import dbconnection.CompanyDao;
import entities.Company;
import entities.NewCompany;
import exceptions.CompanyExistsException;
import input.UserInputService;
import java.util.Comparator;
import java.util.Map;
import program.Session;

public class ManagerUIService extends UIService {

  public ManagerUIService(Session session) {
    super(session);
  }

  @Override
  public String showMenu() {
    String menu = "\n1. Company list\n"
        + "2. Create a company\n"
        + "0. Back";
    System.out.println(menu);
    return menu;
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
      companyDao.save(new NewCompany(UserInputService.getStringInput(scanner)));
      System.out.println("The company was created!");
    } catch (CompanyExistsException e) {
      System.out.println("\nSuch company already exists.");
    }
  }

  @Override
  protected String printIfEmpty() {
    String output = "\nThe company list is empty";
    System.out.println(output);
    return output;
  }
}

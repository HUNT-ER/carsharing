package carsharing.ui;

import carsharing.dbconnection.CompanyDao;
import carsharing.entities.Company;
import carsharing.entities.NewCompany;
import carsharing.exceptions.CompanyExistsException;
import carsharing.input.UserInputService;
import carsharing.program.Session;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainUIService extends UIService{

  public MainUIService(Session session) {
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
        showCompanies();
        break;
      case 2:
        createCompany();
        break;
      case 0:
        session.setMenu(new LogInUIService(session));
        break;
    }
  }

  private void showCompanies() {
    CompanyDao companyDao = new CompanyDao(session.getDbConnection());
    List<Company> companies = companyDao.getAll();

    //сортируем в естественном порядке без учета регистра
    Collections.sort(companies, Comparator.comparing(Company::getId));

    //формируем финальный вывод
    StringBuilder view = new StringBuilder("\n");
    if (companies.size() != 0) {
      view.append("Company list:\n");
      for (int i = 0; i < companies.size(); i++) {
        view.append(i + 1);
        view.append(". ");
        view.append(companies.get(i));
        view.append("\n");
      }
    } else {
      view.append("The company list is empty!\n");
    }
    System.out.print(view);
  }

  private void createCompany() {
    System.out.println("\nEnter the company name:");
    CompanyDao companyDao = new CompanyDao(session.getDbConnection());
    try {
      companyDao.save(new NewCompany(UserInputService.getStringInput()));
      System.out.println("The company was created!");
    } catch (CompanyExistsException e) {
      System.out.println("\nSuch company already exists.");
    }
  }

}

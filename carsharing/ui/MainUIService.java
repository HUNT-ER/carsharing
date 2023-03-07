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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainUIService extends UIService {

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
    List<Company> companies = new CompanyDao(session.getDbConnection()).getAll();
    //сортируем в естественном порядке по id
    Collections.sort(companies, Comparator.comparing(Company::getId));

    Map<Integer, Company> menuChoice = IntStream.range(1, companies.size()+1)
        .boxed().collect(Collectors.toMap(i -> i, i-> companies.get(i-1)));



    //формируем финальный вывод
    StringBuilder view = new StringBuilder("\n");
    if (menuChoice.size() != 0) {
      //распечатываем полностью все компании
      //отдельно показываем кнопку выхода
      System.out.println("\nChoose the company:");
      menuChoice.forEach((k, v) -> System.out.println(k + ". " + v));
      System.out.println("0. Back");

      final int input = UserInputService.getIntInput();
      if (menuChoice.containsKey(input)) {
        //переходим на уровень глубже в меню компании
        session.setMenu(new CompanyUIService(session, menuChoice.get(input)));
      } else if (input == 0) {
        return;
      } else {
        System.out.println("Wrong input.");
        return;
      }
    } else {
      System.out.println("The company list is empty!");
    }
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

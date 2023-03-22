import dbconnection.CarDao;
import entities.Car;
import entities.Company;
import entities.Customer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.Session;
import ui.CustomerUIService;
import ui.LogInUIService;

public class CustomerUIServiceTest {

  private Session session;
  private CustomerUIService customerUI;
  private static final InputStream SYSTEM_IN = System.in;
  private static final PrintStream SYSTEM_OUT = System.out;
  private final static String companies =
      "\nChoose a company:\n1. Sanya\n2. Second\n3. Thirds\n4. SSd\n5. lol\n0. Back"
          + System.lineSeparator();
  private ByteArrayInputStream input;
  private ByteArrayOutputStream output;

  private void setLogInUi(String dbFileName) {
    session = new Session(dbFileName);
    customerUI = new CustomerUIService(session, new Customer(19, "Second",
        new Car(4, "Sanya's the super ultimate car",
            new Company(1, "Sanya"), true)));
  }

  @BeforeEach
  public void setOutput() {
    output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));
  }

  @BeforeEach
  public void initTestDatabase() throws IOException {
    Path testDbFile = Path.of(
        "C:\\Users\\Sasha\\IdeaProjects\\carsharing\\src\\main\\resources\\test.mv.db");
    Path testDbFileCopy = Path.of(
        "C:\\Users\\Sasha\\IdeaProjects\\carsharing\\src\\main\\resources\\test_db.mv.db");
    Files.deleteIfExists(testDbFileCopy);
    Files.copy(testDbFile, testDbFileCopy);

    //Устанавливаем сессию и меню входа
    setLogInUi("test_db");
  }

  @AfterEach
  public void deleteTestDb() throws IOException {
    Path testDbFile = Path.of(
        "C:\\Users\\Sasha\\IdeaProjects\\carsharing\\src\\main\\resources\\test_db.mv.db");
    Files.delete(testDbFile);
  }

  @AfterEach
  public void restoreSystemInOut() {
    System.setIn(SYSTEM_IN);
    System.setOut(SYSTEM_OUT);
  }

  private void provideInput(String in) {
    input = new ByteArrayInputStream(in.getBytes());
    System.setIn(input);
    customerUI.setScanner(new Scanner(input));
    session.setMenu(customerUI);
  }

  private String getOutput() {
    return output.toString();
  }

  @Test
  public void showMenuShouldPrintCustomerMenu() {
    String expectedMenu = "\n1. Rent a car\n2. Return a rented car\n3. My rented car\n0. Back\n";
    Assertions.assertEquals(expectedMenu, customerUI.showMenu());
  }

  @Test
  public void rentCarShouldReturnMessageIfCustomerAlreadyHaveRent() {
    customerUI.chooseMenu(1);
    Assertions.assertEquals("\nYou've already rented a car!" + System.lineSeparator(), getOutput());
  }

  @Test
  public void shouldPrintEmptyListMessageIfNoCompanies() {
    String emptyCompaniesMessage = "\nList is empty!" + System.lineSeparator();
    //Удаляем все компании из тестовой базы
    try (Connection conn = session.getDbConnection().getConnection();
        Statement statement = conn.createStatement()) {
      statement.execute("ALTER TABLE COMPANY DROP CONSTRAINT CONSTRAINT_103");
      statement.execute("DELETE FROM COMPANY");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    customerUI.getCustomer().setCar(null);
    customerUI.chooseMenu(1);
    Assertions.assertEquals(emptyCompaniesMessage, getOutput());
  }

  @Test
  public void rentCarShouldPrintCompanies() {
    customerUI.getCustomer().setCar(null);
    provideInput("0");
    customerUI.chooseMenu(1);
    Assertions.assertEquals(companies, getOutput());
  }

  @Test
  public void shouldPrintEmptyListMessageIfCompanyHasNoCars() {
    String emptyListMessage = "\nList is empty!" + System.lineSeparator();
    customerUI.getCustomer().setCar(null);
    provideInput("3");
    customerUI.chooseMenu(1);
    Assertions.assertEquals(companies + emptyListMessage, getOutput());
  }

  @Test
  public void shouldPrintAllCompanyCars() {
    //Проверяем машины четвертой компании
    String fourthCompanyCars = "\nChoose a car:\n1. Lamborgini\n0. Back" + System.lineSeparator();
    customerUI.getCustomer().setCar(null);
    provideInput("4\n0");
    customerUI.chooseMenu(1);
    Assertions.assertEquals(companies + fourthCompanyCars, getOutput());

    //Проверяем машины второй компании
    setOutput();
    String secondCompanyCars = "\nChoose a car:\n1. Granta\n0. Back" + System.lineSeparator();
    customerUI.getCustomer().setCar(null);
    provideInput("2\n0");
    customerUI.chooseMenu(1);
    Assertions.assertEquals(companies + secondCompanyCars, getOutput());
  }

  @Test
  public void shouldPrintCorrectMessageIfCarWasReturned() {
    String message = "\nYou've returned a rented car!" + System.lineSeparator();
    customerUI.chooseMenu(2);
    Assertions.assertEquals(message, getOutput());
  }

  @Test
  public void shouldPrintCorrectMessageIfHasNoActiveRent() {
    String message = "\nYou didn't rent a car!" + System.lineSeparator();
    customerUI.getCustomer().setCar(null);
    customerUI.chooseMenu(2);
    Assertions.assertEquals(message, getOutput());
  }

  @Test
  public void shouldPrintCurrentRentedCar() {
    String message = "\nYou rented car:\nSanya's the super ultimate car\nCompany:\nSanya"
        + System.lineSeparator();
    customerUI.chooseMenu(3);
    Assertions.assertEquals(message, getOutput());
  }

  @Test
  public void shouldBePrintedInfoAboutNewRentedCar() {
    String secondCompanyCars = "\nChoose a car:\n1. Granta\n0. Back" + System.lineSeparator();
    String rentMessage = "\nYou rented 'Granta'" + System.lineSeparator();
    customerUI.getCustomer().setCar(null);
    provideInput("2\n1\n");
    customerUI.chooseMenu(1);
    Assertions.assertEquals(companies + secondCompanyCars + rentMessage, getOutput());

    String newCarInfo = "\nYou rented car:\nGranta\nCompany:\nSecond"
        + System.lineSeparator();
    setOutput();
    customerUI.chooseMenu(3);
    Assertions.assertEquals(newCarInfo, getOutput());
  }

  @Test
  public void carShouldNotPrintedIfItWasRented() {
    String secondCompanyCars = "\nChoose a car:\n1. Granta\n0. Back" + System.lineSeparator();
    String rentMessage = "\nYou rented 'Granta'" + System.lineSeparator();
    customerUI.getCustomer().setCar(null);
    provideInput("2\n1\n");
    customerUI.chooseMenu(1);

    //Меняем на пользователя без аренды
    customerUI = new CustomerUIService(session, new Customer(18, "First"));
    //У компании не должно быть машин, так как мы забрали единственную
    String emptyListMessage = "\nList is empty!" + System.lineSeparator();
    provideInput("2\n");
    //сбрасываем предыдущий вывод с консоли
    setOutput();
    customerUI.chooseMenu(1);
    Assertions.assertEquals(companies + emptyListMessage, getOutput());
  }

  @Test
  public void menuShouldBeChangedOnLogInMenu() {
    customerUI.chooseMenu(0);
    Assertions.assertTrue(session.getMenu() instanceof LogInUIService);
  }
}

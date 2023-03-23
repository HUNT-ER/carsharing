import entities.Company;
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
import ui.CompanyUIService;
import ui.ManagerUIService;

public class CompanyUIServiceTest {

  private static final InputStream SYSTEM_IN = System.in;
  private static final PrintStream SYSTEM_OUT = System.out;
  private ByteArrayInputStream in;
  private ByteArrayOutputStream out;
  private CompanyUIService companyUi;
  private final static String cars =
      "\nCar list:" + System.lineSeparator() + "1. Granta" + System.lineSeparator();
  private Session session;

  @BeforeEach
  public void initTestDatabaseAndMenu() throws IOException {
    Path testDbFile = Path.of(
        "C:\\Users\\Sasha\\IdeaProjects\\carsharing\\src\\main\\resources\\test.mv.db");
    Path testDbFileCopy = Path.of(
        "C:\\Users\\Sasha\\IdeaProjects\\carsharing\\src\\main\\resources\\test_db.mv.db");
    Files.deleteIfExists(testDbFileCopy);
    Files.copy(testDbFile, testDbFileCopy);

    //Устанавливаем сессию и меню входа
    setCompanyUi("test_db");
  }

  @BeforeEach
  public void setOutput() {
    out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
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

  public void setCompanyUi(String dbName) {
    session = new Session(dbName);
    Company company = new Company(2, "Second");
    companyUi = new CompanyUIService(session, company);
  }

  private void provideInput(String input) {
    in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);
    companyUi.setScanner(new Scanner(input));
    session.setMenu(companyUi);
  }

  private String getOutput() {
    return out.toString();
  }

  @Test
  public void shouldBePrintedCompanyMenu() {
    String menu = "\n'Second' company"
        + "\n1. Car list\n"
        + "2. Create a car\n"
        + "0. Back\n" + System.lineSeparator();
    companyUi.showMenu();
    Assertions.assertEquals(menu, getOutput());
  }

  @Test
  public void shouldPrintEmptyListMessage() throws SQLException {
    String expectedMessage = "\nThe car list is empty!" + System.lineSeparator();
    try (Connection conn = session.getDbConnection().getConnection();
        Statement statement = conn.createStatement()) {
      statement.execute("DELETE FROM CAR WHERE COMPANY_ID = 2");
    } catch (SQLException e) {
      throw new SQLException();
    }
    companyUi.chooseMenu(1);
    Assertions.assertEquals(expectedMessage, getOutput());
  }

  @Test
  public void companyListShouldContainsAllCompanies() {
    provideInput("0\n");
    companyUi.chooseMenu(1);
    Assertions.assertEquals(cars, getOutput());
  }

  @Test
  public void createdCarShouldBeInCaryList() {
    String createCompanyOutput = "\nEnter the car name:" + System.lineSeparator()
        + "The car was added!" + System.lineSeparator();
    String newCompaniesList =
        "\nCar list:" + System.lineSeparator() + "1. Granta" + System.lineSeparator()
            + "2. Vesta" + System.lineSeparator();
    provideInput("Vesta\n0");
    companyUi.chooseMenu(2);
    Assertions.assertEquals(createCompanyOutput, getOutput());
    setOutput();
    companyUi.chooseMenu(1);
    Assertions.assertEquals(newCompaniesList, getOutput());
  }

  @Test
  public void createExistedCompanyShouldBeUnsuccessful() {
    String createCompanyOutput = "\nEnter the car name:" + System.lineSeparator()
        + "\nSuch car already exists." + System.lineSeparator();
    provideInput("Granta\n0");
    companyUi.chooseMenu(2);
    Assertions.assertEquals(createCompanyOutput, getOutput());
    setOutput();
    companyUi.chooseMenu(1);
    Assertions.assertEquals(cars, getOutput());
  }

  @Test
  public void menuShouldBackToManagerMenu() {
    provideInput("0\n");
    companyUi.showUI();
    Assertions.assertTrue(session.getMenu() instanceof ManagerUIService);
  }

}

import entities.Company;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.Session;
import ui.CompanyUIService;
import ui.LogInUIService;
import ui.ManagerUIService;

public class ManagerUIServiceTest {

  private static final InputStream SYSTEM_IN = System.in;
  private static final PrintStream SYSTEM_OUT = System.out;
  private ByteArrayInputStream in;
  private ByteArrayOutputStream out;
  private ManagerUIService managerUI;
  private final static String companies =
      "\nChoose the company:\n1. Sanya\n2. Second\n3. Thirds\n4. SSd\n5. lol\n0. Back"
          + System.lineSeparator();
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
    setManagerUi("test_db");
  }

  public void setManagerUi(String dbName) {
    session = new Session(dbName);
    managerUI = new ManagerUIService(session);
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

  private void provideInput(String input) {
    in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);
    managerUI.setScanner(new Scanner(input));
    session.setMenu(managerUI);
  }

  @BeforeEach
  public void setOutput() {
    out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));
  }

  private String getOutput() {
    return out.toString();
  }

  @Test
  public void shouldBePrintedManagerMenu() {
    String menu = "\n1. Company list\n"
        + "2. Create a company\n"
        + "0. Back" + System.lineSeparator();
    managerUI.showMenu();
    Assertions.assertEquals(menu, getOutput());
  }

  @Test
  public void companyListShouldContainsAllCompanies() {
    provideInput("0\n");
    managerUI.chooseMenu(1);
    Assertions.assertEquals(companies, getOutput());
  }

  @Test
  public void createdCompanyShouldBeInCompanyList() {
    String createCompanyOutput = "Enter the company name:" + System.lineSeparator()
        + "The company was created!" + System.lineSeparator();
    String newCompaniesList =
        "\nChoose the company:\n1. Sanya\n2. Second\n3. Thirds\n4. SSd\n5. lol\n6. New company\n0. Back"
        + System.lineSeparator();
    provideInput("New company\n0");
    managerUI.chooseMenu(2);
    Assertions.assertEquals(createCompanyOutput, getOutput());
    setOutput();
    managerUI.chooseMenu(1);
    Assertions.assertEquals(newCompaniesList, getOutput());
  }

  @Test
  public void createExistedCompanyShouldBeUnsuccessful() {
    String createCompanyOutput = "Enter the company name:" + System.lineSeparator()
        + "\nSuch company already exists." + System.lineSeparator();
    provideInput("Second\n0");
    managerUI.chooseMenu(2);
    Assertions.assertEquals(createCompanyOutput, getOutput());
    setOutput();
    managerUI.chooseMenu(1);
    Assertions.assertEquals(companies, getOutput());
  }

  @Test
  public void transitionToCompanyMenuShouldContainsChosenCompany() {
    provideInput("2\n");
    Company expectedCompany = new Company(2, "Second");
    managerUI.chooseMenu(1);
    Assertions.assertTrue(session.getMenu() instanceof CompanyUIService);
    Assertions.assertEquals(expectedCompany, ((CompanyUIService) session.getMenu()).getCompany());
  }

  @Test
  public void menuShouldBackToLogInMenu() {
    provideInput("0\n");
    managerUI.showUI();
    Assertions.assertTrue(session.getMenu() instanceof LogInUIService);
  }

}

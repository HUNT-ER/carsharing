import dbconnection.CustomerDao;
import dbconnection.Dao;
import entities.Car;
import entities.Company;
import entities.Customer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.Session;
import ui.CustomerUIService;
import ui.LogInUIService;
import ui.ManagerUIService;
import ui.UIService;

public class LogInUIServiceTest {

  private Session session;
  private LogInUIService logInUi;
  private static final InputStream SYSTEM_IN = System.in;
  private static final PrintStream SYSTEM_OUT = System.out;
  private ByteArrayInputStream input;
  private ByteArrayOutputStream output;

  private void setLogInUi(String dbFileName) {
    session = new Session(dbFileName);
    logInUi = new LogInUIService(session);
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

  public Map<Integer, Customer> getTestCustomerMenu() {
    Map<Integer, Customer> testMap = new HashMap<>();
    testMap.put(1, new Customer(18, "First"));
    testMap.put(2, new Customer(19, "Second",
        new Car(4, "Sanya's the super ultimate car",
            new Company(1, "Sanya"), true)));
    testMap.put(3, new Customer(20, "Third",
        new Car(3, "Sanya's car",
            new Company(1, "Sanya"), true)));
    return testMap;
  }

  private void provideInput(String in) {
    input = new ByteArrayInputStream(in.getBytes());
    System.setIn(input);
    logInUi.setScanner(new Scanner(input));
    session.setMenu(logInUi);
  }

  private String getOutput() {
    return output.toString();
  }

  private boolean isEqualsMaps(Map<Integer, Customer> map1, Map<Integer, Customer> map2) {
    if (map1.size() != map2.size()) {
      return false;
    }
    for (Integer key : map1.keySet()) {
      if (!map2.containsKey(key)) {
        return false;
      }
      if (!map1.get(key).equals(map2.get(key))) {
        return false;
      }
    }
    return true;
  }

  private Map<Integer, Customer> getMenu(Dao<Customer> dao, Predicate<Customer> filter,
      Comparator<Customer> order) {
    try {
      Method getMenu = UIService.class.getDeclaredMethod("getMenu", Dao.class, Predicate.class,
          Comparator.class);
      getMenu.setAccessible(true);

      return (HashMap<Integer, Customer>) getMenu.invoke(logInUi, dao, filter, order);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return new HashMap<>();
  }

  @Test
  public void showMenuShouldOutputDefaultMenu() throws IOException {
    String menu = "\n1. Log in as a manager\n"
        + "2. Log in as a customer\n"
        + "3. Create a customer\n"
        + "0. Exit";

    Assertions.assertEquals(menu, logInUi.showMenu());
  }

  @Test
  public void menuShouldSetUpToManagerUiService() {
    logInUi.chooseMenu(1);

    Assertions.assertEquals(true, session.getMenu() instanceof ManagerUIService);
  }

  @Test
  public void customerMenuShouldContainsAllCustomers() {
    Map<Integer, Customer> testMenu = getTestCustomerMenu();

    Map<Integer, Customer> menu = getMenu(new CustomerDao(session.getDbConnection()),
        customer -> true, Comparator.comparing(Customer::getId));
    Assertions.assertEquals(true, isEqualsMaps(testMenu, menu));

  }

  @Test
  public void customerMenuShouldContainsNewCustomer() {
    Map<Integer, Customer> testMenu = getTestCustomerMenu();
    String message = "\nEnter the customer name:" + System.lineSeparator()
        + "\nThe customer was added!" + System.lineSeparator();
    testMenu.put(4, new Customer(21, "Fourth"));
    provideInput("Fourth");
    logInUi.chooseMenu(3);

    Map<Integer, Customer> menu = getMenu(new CustomerDao(session.getDbConnection()),
        customer -> true, Comparator.comparing(Customer::getId));
    Assertions.assertEquals(true, isEqualsMaps(testMenu, menu));
    Assertions.assertEquals(message, getOutput());

  }

  @Test
  public void chooseCustomerShouldPrintExistingMessage() {
    Map<Integer, Customer> testMenu = getTestCustomerMenu();
    String expectedOutput = "\nEnter the customer name:" + System.lineSeparator()
        + "\nSuch customer already exists." + System.lineSeparator();
    provideInput("Third");
    logInUi.chooseMenu(3);
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    Map<Integer, Customer> menu = getMenu(new CustomerDao(session.getDbConnection()),
        customer -> true, Comparator.comparing(Customer::getId));
    Assertions.assertEquals(true, isEqualsMaps(testMenu, menu));
    Assertions.assertEquals(expectedOutput, getOutput());

  }

  @Test
  public void customerMenuShouldBeSortedByName() {
    Map<Integer, Customer> testMenu = getTestCustomerMenu();
    testMenu.put(4, new Customer(21, "Fourth"));
    provideInput("Fourth");
    logInUi.chooseMenu(3);

    Comparator<Customer> sortByNameOrder = Comparator.comparing(Customer::getName);
    List<Customer> customers = new ArrayList<>(testMenu.values());
    customers.sort(sortByNameOrder);

    Map<Integer, Customer> sortedTestMenu = IntStream.range(0, customers.size()).boxed()
        .collect(
            Collectors.toMap(k -> k + 1, k -> customers.get(k)));

    Map<Integer, Customer> menu = getMenu(new CustomerDao(session.getDbConnection()),
        customer -> true, sortByNameOrder);
    System.out.println(menu);
    Assertions.assertEquals(true, isEqualsMaps(sortedTestMenu, menu));
  }

  @Test
  public void customerMenuShouldBeEmpty() {
    try (Connection conn = session.getDbConnection().getConnection();
        Statement statement = conn.createStatement()) {
      statement.execute("DELETE FROM CUSTOMER");

      String emptyListMessage = "\nThe customer list is empty!" + System.lineSeparator();
      logInUi.chooseMenu(2);
      Assertions.assertEquals(emptyListMessage, getOutput());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void sessionMenuShouldBeChangedOnCustomerUiServiceAndHaveSameCustomer() {
    Map<Integer, Customer> testMenu = getTestCustomerMenu();
    try {
      Method chooseCustomer = LogInUIService.class.getDeclaredMethod("chooseCustomer", null);
      chooseCustomer.setAccessible(true);
      provideInput("2");
      chooseCustomer.invoke(logInUi);
      //проверяем, чтобы менялось на меню конкретного покупателя
      Assertions.assertEquals(true, session.getMenu() instanceof CustomerUIService);
      Assertions.assertEquals(testMenu.get(2),
          ((CustomerUIService) session.getMenu()).getCustomer());
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void sessionShouldBeClosed() {
    logInUi.chooseMenu(0);
    Assertions.assertEquals(true, session.isClosed());
  }

  @Test
  public void shouldBePrintedPreviousMenuIfInputIsZero() {
    String menu = "\n1. Log in as a manager\n"
        + "2. Log in as a customer\n"
        + "3. Create a customer\n"
        + "0. Exit" + System.lineSeparator();

    String customerCreation = "\nEnter the customer name:" + System.lineSeparator()
        + "\nThe customer was added!" + System.lineSeparator();

    String customerList = "\nCustomer list:\n"
        + "1. First\n"
        + "2. Second\n"
        + "3. Third\n"
        + "4. nh\n"
        + "0. Back" + System.lineSeparator();

    String input = "3" + System.lineSeparator() + "nh" + System.lineSeparator()
        + "2" + System.lineSeparator() + "0" + System.lineSeparator();
    provideInput(input);

    for (int i = 0; i < 2; i++) {
      logInUi.showUI();
    }

    Assertions.assertEquals(menu + customerCreation + menu + customerList, getOutput());
  }
}

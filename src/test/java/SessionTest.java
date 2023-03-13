import dbconnection.H2Database;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.Session;
import ui.LogInUIService;
import ui.ManagerUIService;
import ui.UIService;

public class SessionTest {

  private Session sessionWithoutArg;
  private Session sessionWithArg;

  @BeforeEach
  public void sessionWithArgs() {
    sessionWithArg = new Session("test");
  }

  @BeforeEach
  public void setSessionWithoutArgs() {
    sessionWithoutArg = new Session();
  }

  @Test
  public void newSessionShouldBeNotClosed() {

    Assertions.assertEquals(false, sessionWithoutArg.isClosed());
  }

  @Test
  public void newSessionShouldHasLoginMenuUI() {

    Assertions.assertEquals(true, sessionWithoutArg.getMenu() instanceof LogInUIService);
  }

  @Test
  public void closeSessionShouldChangeIsClosedFlag() {
    sessionWithoutArg.close();

    Assertions.assertEquals(true, sessionWithoutArg.isClosed());
  }

  @Test
  public void setMenuShouldChangeMenu() {
    UIService ui = new ManagerUIService(sessionWithoutArg);
    sessionWithoutArg.setMenu(ui);

    Assertions.assertEquals(ui, sessionWithoutArg.getMenu());
  }

  @Test
  public void newSessionWithoutArgShouldHAveDefaultDb() {
    Assertions.assertEquals(new H2Database(), sessionWithoutArg.getDbConnection());
  }

  @Test
  public void newSessionWithArgShouldHaveDbFromFileNameIfFileExists() {
    Assertions.assertEquals(new H2Database("test"), sessionWithArg.getDbConnection());
  }

  @Test
  public void newSessionWithArgShouldHaveDefaultDbIfFileNotExists() {
    Session session = new Session("sdk");
    Assertions.assertEquals(new H2Database("cars"), session.getDbConnection());
  }

  @Test
  public void sessionShouldCloseWhenEnterExitValue() {
    InputStream sysIn = System.in;
    ByteArrayInputStream input = new ByteArrayInputStream("0".getBytes());
    System.setIn(input);
    sessionWithoutArg.start();

    Assertions.assertEquals(true, sessionWithoutArg.isClosed());
  }
}
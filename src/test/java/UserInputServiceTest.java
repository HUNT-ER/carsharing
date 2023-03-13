import static input.UserInputService.getIntInput;
import static input.UserInputService.getStringInput;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UserInputServiceTest {

  private static InputStream sysInput;

  @BeforeAll
  public static void backupSystemIn() {
    sysInput = System.in;
  }

  @AfterAll
  public static void restoreSystemIn() {
    System.setIn(sysInput);
  }

  @Test
  public void getIntInputShouldReturnDefaultValueIfInputIsString() {    // default value is -1;
    ByteArrayInputStream inputString = new ByteArrayInputStream("st".getBytes());
    System.setIn(inputString);

    Assertions.assertEquals(-1, getIntInput());
  }

  @Test
  public void getIntInputShouldReturnDefaultValueIfInputIsNegative() {
    ByteArrayInputStream inputNegativeInt = new ByteArrayInputStream("-2".getBytes());
    System.setIn(inputNegativeInt);

    Assertions.assertEquals(-1, getIntInput());
  }

  @Test
  public void getIntInputShouldReturnDefaultValueIfInputIsLong() {
    ByteArrayInputStream inputNegativeInt = new ByteArrayInputStream("100000000000000".getBytes());
    System.setIn(inputNegativeInt);

    Assertions.assertEquals(-1, getIntInput());
  }

  @Test
  public void getIntInputShouldReturnSameIntValue() {
    ByteArrayInputStream in = new ByteArrayInputStream("100".getBytes());
    System.setIn(in);

    Assertions.assertEquals(100, getIntInput());
  }

  @Test
  public void getStringInputShouldReturnSameString() {
    ByteArrayInputStream in = new ByteArrayInputStream("sad".getBytes());
    System.setIn(in);

    Assertions.assertEquals("sad", getStringInput());
  }

  @Test
  public void getStringInputShouldReturnEmptyStringIfInputIsEmpty() {
    ByteArrayInputStream in = new ByteArrayInputStream("".getBytes());
    System.setIn(in);

    Assertions.assertEquals("", getStringInput());
  }

}

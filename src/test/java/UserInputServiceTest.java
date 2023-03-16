import static input.UserInputService.getIntInput;
import static input.UserInputService.getStringInput;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserInputServiceTest {

  private static InputStream sysInput = System.in;
  private Scanner scanner;

  @BeforeEach
  public void setScanner() {
    scanner = new Scanner(System.in);
  }

  @AfterAll
  public static void restoreSystemIn() {
    System.setIn(sysInput);
  }

  public void provideInput(String input) {
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);
    scanner = new Scanner(in);
  }

  @Test
  public void getIntInputShouldReturnDefaultValueIfInputIsString() {
    provideInput("string");
    // default value is -1;
    Assertions.assertEquals(-1, getIntInput(scanner));
  }

  @Test
  public void getIntInputShouldReturnDefaultValueIfInputIsNegative() {
    provideInput("-2");
    Assertions.assertEquals(-1, getIntInput(scanner));
  }

  @Test
  public void getIntInputShouldReturnDefaultValueIfInputIsLong() {
    provideInput("100000000000000");
    Assertions.assertEquals(-1, getIntInput(scanner));
  }

  @Test
  public void getIntInputShouldReturnSameIntValue() {
    provideInput("100");
    Assertions.assertEquals(100, getIntInput(scanner));
  }

  @Test
  public void getStringInputShouldReturnSameString() {
    provideInput("sad");
    Assertions.assertEquals("sad", getStringInput(scanner));
  }

  @Test
  public void getStringInputShouldReturnEmptyStringIfInputIsEmpty() {
    provideInput(System.lineSeparator());

    Assertions.assertEquals("", getStringInput(scanner));
  }

  @Test
  public void multipleInputShouldNotContainsOtherInputs() {
    provideInput("2\n3\nsdk");

    int i = getIntInput(scanner);
    int j = getIntInput(scanner);
    String s = getStringInput(scanner);
    String sd = getStringInput(scanner);

    System.out.println(i + " " + j + " " + s + sd);
  }

}

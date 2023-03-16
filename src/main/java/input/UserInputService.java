package input;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UserInputService {

  public static int getIntInput(Scanner scanner) {
    int input = -1;
    if (scanner.hasNext()) {
      try {
        input = scanner.nextInt();
        if (input < -1) {
          return -1;
        }
      } catch (InputMismatchException e) {
        return input;
      }
    }
    return input;
  }

  public static String getStringInput(Scanner scanner) {
    if (scanner.hasNextLine()) {
      String input = scanner.nextLine();
      return ("".equals(input) ? (scanner.hasNextLine() ? scanner.nextLine() : input) : input);
    }
    return "";
  }
}

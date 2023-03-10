package input;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UserInputService {

  public static int getIntInput() {
    Scanner scanner = new Scanner(System.in);
    int input = -1;
    try {
      input = scanner.nextInt();
    } catch (InputMismatchException e) {
      return input;
    }
    return input;
  }

  public static String getStringInput() {
    Scanner scanner = new Scanner(System.in);
    return scanner.nextLine();
  }
}

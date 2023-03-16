package ui;

import dbconnection.Dao;
import entities.Entity;
import input.UserInputService;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import program.Session;

public abstract class UIService {

  protected Session session;

  public void setScanner(Scanner scanner) {
    this.scanner = scanner;
  }

  protected Scanner scanner;

  public UIService(Session session) {
    this.session = session;
    this.scanner = new Scanner(System.in);
  }

  public void showUI() {
    showMenu();
    chooseMenu(UserInputService.getIntInput(scanner));
  }

  public abstract String showMenu();

  public abstract void chooseMenu(int choice);

  //1. Объект базы данных
  //2. Предикат, какие объекты будут возвращены
  //3. По какому принципу их отсортировать в итоговой мапе
  public <K extends Integer, V extends Entity> Map<K, V> getMenu(Dao<V> dao,
      Predicate<V> filterPredicate, Comparator<V> sortingComparator) {
    //отбираем сущности из базы данных и сортируем по указанному признаку для отображения
    List<V> elements = dao.getAll().stream()
        .filter(filterPredicate)
        .sorted(sortingComparator)
        .collect(Collectors.toList());

    //заполняем сущностями меню
    Map<K, V> menuChoice = (HashMap) IntStream.range(1, elements.size() + 1)
        .boxed().collect(Collectors.toMap(i -> i, i -> elements.get(i - 1)));

    return menuChoice;
  }

  //печатаем меню с возможностью выбора подпунктов
  protected <K extends Integer, V extends Entity> String printMenuAndMakeChoice(String title,
      Map<K, V> menu, Consumer<Integer> action) {
    StringBuilder outputMenu = new StringBuilder();
    if (menu.size() != 0) {
      //печатаем шапку меню и само меню

      outputMenu.append("\n" + title + "\n");
      menu.forEach((k, v) -> outputMenu.append(k + ". " + v + "\n"));
      outputMenu.append("0. Back");
      System.out.println(outputMenu);

      final int input = UserInputService.getIntInput(scanner);
      if (menu.containsKey(input)) {
        //выполняем действие над выбранным объектом
        action.accept(input);
        return outputMenu.toString();
      } else if (input == 0) {
        return outputMenu.toString();
      } else {
        System.out.println("Wrong input.");
        return "Wrong input.";
      }
    } else {
      return printIfEmpty();
    }
  }

  protected <K extends Integer, V extends Entity> void printMenu(String title, Map<K, V> menu) {
    //печатаем меню без возможности выбора подпунктов
    if (menu.size() != 0) {
      System.out.println("\n" + title);
      menu.forEach((k, v) -> System.out.println(k + ". " + v));
    } else {
      printIfEmpty();
    }
  }

  protected String printIfEmpty() {
    String output = "\nList is empty!";
    System.out.println(output);
    return output;
  }
}

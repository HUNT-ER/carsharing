package ui;

import dbconnection.Dao;
import entities.Entity;
import input.UserInputService;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import program.Session;

public abstract class UIService {

  protected Session session;

  public UIService(Session session) {
    this.session = session;
  }

  public void showUI() {
    showMenu();
    chooseMenu(UserInputService.getIntInput());
  }

  public abstract void showMenu();

  public abstract void chooseMenu(int choice);
  public <K extends Integer, V extends Entity> Map<K, V> getMenu(Dao<V> dao,
      Predicate<V> filterPredicate, Comparator<V> sortingComparator) {
    //отбираем сущности из базы данных
    List<V> elements = dao.getAll().stream().filter(filterPredicate).collect(Collectors.toList());

    //Сортируем по указанному признаку для отображения
    elements.sort(sortingComparator);

    //заполняем сущностями меню
    Map<K, V> menuChoice = (HashMap) IntStream.range(1, elements.size() + 1)
        .boxed().collect(Collectors.toMap(i -> i, i -> elements.get(i - 1)));

    return menuChoice;
  }

  protected <K extends Integer, V extends Entity> void printMenuAndMakeChoice(String title,
      Map<K, V> menu, Consumer<Integer> action) {
    if (menu.size() != 0) {
      //печатаем шапку меню и само меню
      System.out.println("\n" + title);
      menu.forEach((k, v) -> System.out.println(k + ". " + v));
      System.out.println("0. Back");

      final int input = UserInputService.getIntInput();
      if (menu.containsKey(input)) {
        //выполняем действие над выбранным объектом
        action.accept(input);
      } else if (input == 0) {
        return;
      } else {
        System.out.println("Wrong input.");
        return;
      }
    } else {
      printIfEmpty();
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

  protected void printIfEmpty() {
    System.out.println("\nList is empty!");
  }
}

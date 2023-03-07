package carsharing.entities;

public class NewCar extends Car {
  //у новой Car отсутсвует ID, присваивается при внесениии в базу данных
  public NewCar(String name, Company company) {
    super(0, name, company);
  }
}

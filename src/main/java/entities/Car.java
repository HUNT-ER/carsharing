package entities;

public class Car extends Entity {

  private Company company;
  private boolean isRented;
  public Car(int id, String name, Company company, boolean isRented) {
    this.id = id;
    this.name = name;
    this.company = company;
    this.isRented = isRented;
  }

  public Company getCompany() {
    return company;
  }
  public boolean isRented() {
    return isRented;
  }
}

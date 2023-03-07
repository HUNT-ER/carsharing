package carsharing.entities;

public class Car {
  private int id;
  private String name;
  private Company company;

  public Car(int id, String name, Company company) {
    this.id = id;
    this.name = name;
    this.company = company;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Company getCompany() {
    return company;
  }

  @Override
  public String toString() {
    return name;
  }
}

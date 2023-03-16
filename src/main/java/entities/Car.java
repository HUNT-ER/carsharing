package entities;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Car)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    Car car = (Car) o;
    return isRented == car.isRented && company.equals(car.company);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), company, isRented);
  }
}

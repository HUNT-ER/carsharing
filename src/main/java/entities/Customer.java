package entities;

import java.util.Objects;

public class Customer extends Entity {

  private Car car;

  public Customer(int id, String name, Car car) {
    this.id = id;
    this.car = car;
    this.name = name;
  }

  public Customer(int id, String name) {
    this.id = id;
    this.name = name;
    this.car = null;
  }

  public boolean hasActiveRent() {
    return car == null ? false : true;
  }

  public Car getCar() {
    return car;
  }

  public void setCar(Car car) {
    this.car = car;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Customer)) {
      return false;
    }
    Customer customer = (Customer) o;
    return Objects.equals(car, customer.car);
  }

  @Override
  public int hashCode() {
    return Objects.hash(car);
  }
}

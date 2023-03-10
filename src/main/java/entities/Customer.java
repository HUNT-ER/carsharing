package entities;

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

}

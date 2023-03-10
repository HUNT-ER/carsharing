package entities;

public class NewCustomer extends Customer {

  //у нового Customer отсутсвует ID, присваивается при внесениии в базу данных
  public NewCustomer(String name) {
    super(0, name, null);
  }
}

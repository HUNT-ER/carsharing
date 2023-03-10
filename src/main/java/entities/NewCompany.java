package entities;

public class NewCompany extends Company {

  //у новой Company отсутсвует ID, присваивается при внесениии в базу данных
  public NewCompany(String name) {
    super(0, name);
  }
}

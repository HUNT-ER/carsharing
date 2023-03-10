package exceptions;

public class CompanyExistsException extends Exception {
  public CompanyExistsException(String massage) {
    super(massage);
  }
}

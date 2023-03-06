package carsharing.dbconnection;

import carsharing.entities.Company;
import carsharing.exceptions.CompanyExistsException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompanyDao implements Dao<Company> {

  H2Database database;

  public CompanyDao(H2Database database) {
    this.database = database;
  }

  @Override
  public void save(Company company) throws CompanyExistsException {
    final String insertQuery = "INSERT INTO COMPANY(NAME) VALUES(?)";
    try (Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(insertQuery)) {
      statement.setString(1, company.getName());
      statement.executeUpdate();
    } catch (SQLException e) {
      //выбрасываем исключение, если компания уже существует
      throw new CompanyExistsException("[SAVE ERROR]: Such company already exists.");
    }
  }

  @Override
  public Optional<Company> get(int id) {
    final String query = "SELECT * FROM COMPANY WHERE ID = ?";
    try (Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, id);
      ResultSet result = statement.executeQuery();
      if (result.next()) {
        return Optional.of(new Company(result.getInt(1), result.getString(2)));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  @Override
  public void update(Company company) {
    final String query = "UPDATE COMPANY SET NAME = ? WHERE ID = ?";
    try (Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {

      statement.setInt(2, company.getId());
      statement.setString(1, company.getName());
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void delete(Company company) {
    final String query = "DELETE FROM COMPANY  WHERE ID = ?";
    try (Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, company.getId());
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public List<Company> getAll() {
    final String query = "SELECT * FROM COMPANY";
    List<Company> companies = new ArrayList<>();
    try (Connection connection = database.getConnection();
        Statement statement = connection.createStatement()) {

      ResultSet result = statement.executeQuery(query);
      while (result.next()) {
        companies.add(new Company(result.getInt(1), result.getString(2)));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return companies;
  }
}

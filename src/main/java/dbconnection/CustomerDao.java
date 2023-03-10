package dbconnection;

import entities.Car;
import entities.Customer;
import exceptions.CustomerExistsException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDao implements Dao<Customer> {

  H2Database database;

  public CustomerDao(H2Database database) {
    this.database = database;
  }

  @Override
  public void save(Customer customer) throws CustomerExistsException {
    final String insertQuery = "INSERT INTO CUSTOMER(NAME) VALUES(?)";
    try (Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(insertQuery)) {
      statement.setString(1, customer.getName());
      statement.executeUpdate();
    } catch (SQLException e) {
      //выбрасываем исключение, если компания уже существует
      throw new CustomerExistsException("[SAVE ERROR]: Such company already exists.");
    }
  }

  @Override
  public Optional<Customer> get(int id) {
    final String query = "SELECT * FROM CUSTOMER WHERE ID = ?";
    try (Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, id);
      ResultSet result = statement.executeQuery();
      CarDao carDao = new CarDao(database);
      if (result.next()) {
        return Optional.of(
            new Customer(result.getInt(1), result.getString(2),
                carDao.get(result.getInt(3)).get()));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  @Override
  public void update(Customer customer) {
    final String query = "UPDATE CUSTOMER SET NAME = ?, RENTED_CAR_ID = ? WHERE ID = ?";
    try (Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, customer.getName());
      statement.setInt(3, customer.getId());
      if (customer.getCar() == null) {
        statement.setString(2, null);
      } else {
        statement.setInt(2, customer.getCar().getId());
      }
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void delete(Customer customer) {
    final String query = "DELETE FROM CUSTOMER  WHERE ID = ?";
    try (Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, customer.getId());
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public List<Customer> getAll() {
    final String query = "SELECT * FROM CUSTOMER";
    List<Customer> customers = new ArrayList<>();
    try (Connection connection = database.getConnection();
        Statement statement = connection.createStatement()) {

      ResultSet result = statement.executeQuery(query);
      CarDao carDao = new CarDao(database);
      while (result.next()) {
        Optional<Car> car = carDao.get(result.getInt(3));
        if (car.isPresent()) {
          customers.add(new Customer(
              result.getInt(1), result.getString(2),
              carDao.get(result.getInt(3)).get()));
          continue;
        } else {
          customers.add(new Customer(result.getInt(1), result.getString(2)));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return customers;
  }
}

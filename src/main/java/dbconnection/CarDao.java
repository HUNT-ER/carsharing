package dbconnection;

import entities.Car;
import exceptions.CarExistsException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarDao implements Dao<Car> {

  H2Database database;

  public CarDao(H2Database database) {
    this.database = database;
  }

  @Override
  public void save(Car car) throws CarExistsException {
    final String insertQuery = "INSERT INTO CAR(NAME, COMPANY_ID) VALUES(?,?)";
    try (Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(insertQuery)) {
      statement.setString(1, car.getName());
      statement.setInt(2, car.getCompany().getId());
      statement.executeUpdate();
    } catch (SQLException e) {
      //выбрасываем исключение, если компания уже существует
      throw new CarExistsException("[SAVE ERROR]: Such company already exists.");
    }
  }

  @Override
  public Optional<Car> get(int id) {
    final String query = ""
        + "SELECT C.*\n"
        + "    , CASE WHEN C1.RENTED_car_id is not null then true else false end as is_rented\n"
        + "FROM CAR C\n"
        + "LEFT JOIN (SELECT DISTINCT RENTED_CAR_ID FROM CUSTOMER) C1\n"
        + "    ON C1.RENTED_CAR_ID = C.ID\n"
        + "WHERE C.ID = ?";
    try (Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, id);
      ResultSet result = statement.executeQuery();
      CompanyDao companyDao = new CompanyDao(database);
      if (result.next()) {
        return Optional.of(
            new Car(result.getInt(1), result.getString(2),
                companyDao.get(result.getInt(3)).get(), result.getBoolean(4)));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  @Override
  public void update(Car car) {
    final String query = "UPDATE CAR SET NAME = ?, COMPANY_ID = ? WHERE ID = ?";
    try (Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {

      statement.setString(1, car.getName());
      statement.setInt(2, car.getCompany().getId());
      statement.setInt(3, car.getId());

      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void delete(Car car) {
    final String query = "DELETE FROM CAR  WHERE ID = ?";
    try (Connection connection = database.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setInt(1, car.getId());
      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public List<Car> getAll() {
    final String query = ""
        + "SELECT C.*\n"
        + "    , CASE WHEN C1.RENTED_car_id is not null then true else false end as is_rented\n"
        + "FROM CAR C\n"
        + "LEFT JOIN (SELECT DISTINCT RENTED_CAR_ID FROM CUSTOMER) C1\n"
        + "    ON C1.RENTED_CAR_ID = C.ID;\n";
    List<Car> cars = new ArrayList<>();
    try (Connection connection = database.getConnection();
        Statement statement = connection.createStatement()) {

      ResultSet result = statement.executeQuery(query);
      CompanyDao companyDao = new CompanyDao(database);
      while (result.next()) {
        cars.add(new Car(result.getInt(1), result.getString(2),
            companyDao.get(result.getInt(3)).get(), result.getBoolean(4)));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return cars;
  }


}

package carsharing.dbconnection;

import carsharing.exceptions.CompanyExistsException;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {
   //CRUD
   void save(T t) throws Exception;
   Optional<T> get(int id);
   void update(T t);
   void delete(T t);
   List<T> getAll();
}

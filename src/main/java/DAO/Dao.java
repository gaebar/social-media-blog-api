// The generic inteface allows us to define a Data Access Object (DAO) for any
// type of model object. We can then create a specific class for each model object
// that implements this interface.

package DAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    // Define CRUD (Create, Retrieve, Update, Delete) operations
    Optional<T> get(int id) throws SQLException; // Retrieve an object by its id.
    List<T> getAll() throws SQLException; // Retrieve all object in the system.
    T insert(T t) throws SQLException; // Insert a new object into the system and return it.
    boolean update(T t) throws SQLException; // Update an exiting object in the system.
    boolean delete(T t) throws SQLException; // Delete an object from system.
}

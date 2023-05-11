// The generic inteface allows us to define a Data Access Object (DAO) for any
// type of model object. We can then create a specific class for each model object
// that implements this interface.

package DAO;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    // Define CRUD (Create, Retrieve, Update, Delete) operations
    Optional<T> get(long id); // Retrieve an object by its id.
    List<T> getAll(); // Retrieve all object in the system.
    T insert(T t); // Insert a new object into the system and return it.
    void update(T t); // Update an exiting object in the system.
    void delete(T t); // Delete an object from system.
}

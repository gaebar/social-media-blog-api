package DAO;

import java.util.List;
import java.util.Optional;

/**
 * This Generic Data Access Object (DAO) interface provides an abstraction for
 * performing CRUD (Create, Read, Update, Delete) operations
 * on any type of model object. Each model object can have a specific class
 * implementing this interface.
 */

public interface BaseDao<T> {

    // Retrieve an object by its id.
    // @param id The id of the object to retrieve.
    // @return The object, if found, wrapped in an Optional; otherwise, an empty
    // Optional.
    Optional<T> getById(int id);

    // Retrieve all objects in the system.
    // @return A List containing all instances of the model object T present in the
    // database.
    // The order of the objects in the List matches the order of the records in the
    // database.
    List<T> getAll();

    // Retrieve all objects in the system.
    // @param t The object of type T to insert into the database.
    // @return The inserted object, which may include modifications made by the
    // database, such as an auto-generated ID.
    T insert(T t);

    // Update an existing object in the system.
    // @param t The object of type T to update in the database.
    // @return true if the update was successful; false if the object was not found
    // in the database.
    boolean update(T t);

    // Delete an object from the system.
    // @param t The object of type T to delete from the database.
    // @return true if the deletion was successful; false if the object was not
    // found in the database.
    boolean delete(T t);
}

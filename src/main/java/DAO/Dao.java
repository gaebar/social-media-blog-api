package DAO;

import java.util.List;
import java.util.Optional;

// This Generic Data Access Object (DAO) interface allows us to define CRUD operations for any type of model object.
// We can create a specific class for each model object that implements this interface.

public interface Dao<T> {
    // Define CRUD (Create, Retrieve, Update, Delete) operations

    // Retrieve an object by its id. Optional is used here to allow for a null-safe
    // way to indicate that an object with a certain id might not exist in the
    // database.
    // If the object is found, it is wrapped in an Optional,
    // if not, an empty Optional is returned.
    Optional<T> get(int id);

    // Retrieve all objects in the system. A List is used here because there can be
    // multiple records in the database for type T, and we want to retrieve all of
    // them.
    // The List will contain one instance of T for each record in the database.
    // The order of the objects in the List matches the order of the records in the
    // database.
    List<T> getAll();

    // Insert a new object into the system and return it.
    // The parameter is of type T because this is the object that we want to insert
    // into the database.
    // The method returns an object of type T because after inserting the object,
    // it's common to return the inserted object with any modifications made by the
    // database,
    // such as an auto-generated ID.
    T insert(T t);

    // Update an existing object in the system. The method returns true if the
    // update was successful, and false otherwise (for example, if the object
    // does not exist in the database).
    // This allows the caller to check if the object was actually present and able
    // to be updated.
    boolean update(T t);

    // Delete an object from the system. The method returns true if the deletion was
    // successful, and false otherwise (for example, if the object does not exist in
    // the database).
    // This allows the caller to check if the object was actually present and able
    // to be deleted.
    boolean delete(T t);
}

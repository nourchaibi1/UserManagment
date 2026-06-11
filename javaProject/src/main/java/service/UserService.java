package service;
import java.util.List;

public interface UserService<T>{
    void add(T entity);             // To add an entity (e.g., user)
    void delete(int id);            // To delete an entity by its ID
    void modify(T entity);          // To modify an existing entity
    List<T> display();              // To display all entities
    T search(int id);
}

package lab.model.bridge;
import lab.TaskInfo;
import java.util.*;
import lab.exception.*;
/**
 * @author serg
 * @version 1.0
 * @created 06-Сер-2011 14:05:01
 * interface list all method for the access to the Data Base.
 */
public interface Bridge {
     /**
     * Remove task.
     * @param id remove task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeTask(long id) throws DataAccessException; 
    /**
     * Add task
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void addTask(TaskInfo task) throws DataAccessException;
    /**
    * Get task from file.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public TaskInfo getTask(long id) throws DataAccessException;
     /**
     * Remove All task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeAll() throws DataAccessException; 
    /**
    * Load all tasks from file.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public Hashtable<Long,TaskInfo> getAll() throws DataAccessException;
    /**
    * Edit task
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public void editTask(long id, TaskInfo task) throws DataAccessException;
}
package lab.model.bridge;
import lab.TaskInfo;
import java.util.*;
import lab.exception.*;
/**
 * @author serg
 * @version 1.0
 * @created 06-Сер-2011 14:05:01
 */
public interface Bridge {

     /**
     * Remove task.
     * @param id remove task.
     */
    public void removeTask(long id) throws BDException; 

    /**
     * Add task
     */

    public void addTask(TaskInfo task) throws BDException;

    /**
    * Get task from file.
    */
    public TaskInfo getTask(long id) throws BDException;
     /**
     * Remove All task..
     */
    public void removeAll() throws BDException; 
    /**
    * returns rows count
    */
    public long getCountID() throws BDException; 
    /**
    * Load all tasks from file.
    */
    public Hashtable<Long,TaskInfo> getAll() throws BDException;
    /**
    * Edit task
    */
    public void editTask(long id, TaskInfo task) throws BDException;
}
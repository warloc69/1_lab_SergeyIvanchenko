package lab.model.bridge;
import lab.TaskInfo;
import java.util.*;
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
    public void removeTask(long id); 

    /**
     * Add task
     */

    public void addTask(TaskInfo task);

    /**
    * Get task from file.
    */
    public TaskInfo getTask(long id);
     /**
     * Remove All task..
     */
    public void removeAll(); 
    /**
    * returns rows count
    */
    public long getCountID(); 
    /**
    * Load all tasks from file.
    */
    public Hashtable<Long,TaskInfo> getAll();
    /**
    * Edit task
    */
    public void editTask(long id, TaskInfo task);
}
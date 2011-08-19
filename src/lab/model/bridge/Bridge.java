package lab.model.bridge;
import lab.TaskInfo;
import java.util.*;
import java.io.File;
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
    public void removeTask(int id); 

    /**
     * Add task
     */

    public void addTask(TaskInfo task);

    /**
    * Get task from file.
    */
    public TaskInfo getTask(int id);
     /**
     * Remove All task..
     */
    public void removeAll(); 
    /**
    * returns rows count
    */
    public int getCountID(); 
    /**
    * Load all tasks from file.
    */
    public Hashtable<Integer,TaskInfo> getAll();
    /**
    * Edit task
    */
    public void editTask(int id, TaskInfo task);
}
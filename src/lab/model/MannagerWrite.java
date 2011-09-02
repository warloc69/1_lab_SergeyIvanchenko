package lab.model;
import lab.TaskInfo;
import lab.exception.*;
public interface MannagerWrite {

    /**
     * Remove task.
     * @param id remove task.
     */
    public void removeTask(long id) throws DataAccessException;

    /**
     * Add task
     */
    public void addTask(TaskInfo taskinfo) throws DataAccessException;    
    /**
    * Edit task
    */
    public void editTask(long id, TaskInfo task) throws DataAccessException;
}
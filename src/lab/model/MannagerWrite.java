package lab.model;
import lab.TaskInfo;
import lab.exception.*;
/**
* interface list method for the writing information to the model about tasks.
*/
public interface MannagerWrite {
    /**
     * Remove task.
     * @param id removeing task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeTask(long id) throws DataAccessException;
    /**
     * Add task
     * @param taskinfo reference on the adding task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void addTask(TaskInfo taskinfo) throws DataAccessException;    
    /**
    * Edit task
    * @param task reference on the editing task.
    * @param id editing task.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public void editTask(long id, TaskInfo task) throws DataAccessException;
}
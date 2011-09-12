package lab.conntroller;
import lab.*;
import lab.model.*;
import lab.exception.*;
/**
* interface list method for the controller.
*/
public interface ManagerControllerInterface {
    /**
     * Add task
     * @throws DataAccessException if we can't have access to Data Base.
     * throws BadTaskException if task is invalide.
     */
    public void addTask(TaskInfo task) throws BadTaskException, DataAccessException ;

     /**
     * Remove task.
     * @param id remove task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void delTask(long id)throws DataAccessException ;

    /**
    * Edit task
    * @throws DataAccessException if we can't have access to Data Base.
     * throws BadTaskException if task is invalide.
    */
    public void editTask(long id, TaskInfo task) throws BadTaskException, DataAccessException ;
    /**
    * insert model into controller.
    */
    public void setModel(MannagerWrite model);

}
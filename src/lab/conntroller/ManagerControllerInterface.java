package lab.conntroller;
import lab.*;
import lab.model.*;
import lab.exception.*;

public interface ManagerControllerInterface {

    /**
     * Add task
     */
    public void addTask(TaskInfo task) throws BadTaskException, DataAccessException ;

     /**
     * Remove task.
     * @param id remove task.
     */
    public void delTask(long id)throws DataAccessException ;

    /**
    * Edit task
    */
    public void editTask(long id, TaskInfo task) throws BadTaskException, DataAccessException ;
    /**
    * insert model into controller.
    */
    public void setModel(MannagerWrite model);

}
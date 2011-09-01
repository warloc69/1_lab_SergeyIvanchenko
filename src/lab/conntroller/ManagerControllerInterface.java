package lab.conntroller;
import lab.*;
import lab.model.*;
import lab.exception.*;

public interface ManagerControllerInterface {

    /**
     * Add task
     */
    public boolean addTask(TaskInfo task) throws BadTaskException, BDException ;

     /**
     * Remove task.
     * @param id remove task.
     */
    public void delTask(long id)throws BDException ;

    /**
    * Edit task
    */
    public boolean editTask(long id, TaskInfo task) throws BadTaskException, BDException ;
    /**
    * insert model into controller.
    */
    public void setModel(MannagerWrite model) throws BadTaskException, BDException ;

}
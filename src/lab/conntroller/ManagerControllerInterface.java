package lab.conntroller;
import lab.*;
import lab.model.*;

public interface ManagerControllerInterface {

    /**
     * Add task
     */
    public boolean addTask(TaskInfo task);

     /**
     * Remove task.
     * @param id remove task.
     */
    public void delTask(int id);

    /**
    * Edit task
    */
    public boolean editTask(int id, TaskInfo task);
    /**
    * insert model into controller.
    */
    public void setModel(MannagerWrite model);

}
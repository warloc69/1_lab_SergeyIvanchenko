package lab.conntroller;
import lab.model.MannagerWrite;
import lab.*;

public class ManagerController implements ManagerControllerInterface {

    private MannagerWrite model;

    public ManagerController(){

    }

    /**
     * Add task
     */
    public void addTask(TaskInfo task){
        model.addTask(task);
        
    }

     /**
     * Remove task.
     * @param id remove task.
     */
    public void delTask(int id){
        model.removeTask(id);
    }

    /**
    * Edit task
    */
    public void editTask(int id, TaskInfo task){
        model.editTask(id,task) ;
    }
    /**
    * insert model into controller.
    */
    public void setModel(MannagerWrite model) {
        this.model = model;
    }

}//end ManagerController
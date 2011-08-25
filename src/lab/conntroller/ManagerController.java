package lab.conntroller;
import lab.model.MannagerWrite;
import lab.*;
import java.util.Date;

public class ManagerController implements ManagerControllerInterface {

    private MannagerWrite model;

    public ManagerController(){

    }
    /**
     * Add task
     */
    public boolean addTask(TaskInfo task){
        if (validDate(task)) {
            model.addTask(task);
            return true;
        } else {
            return false;
        }
        
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
    public boolean editTask(int id, TaskInfo task){
        if (validDate(task)) {
            model.editTask(id,task);
            return true;
        } else {
            return false;
        }
    }
    /**
    * insert model into controller.
    */
    public void setModel(MannagerWrite model) {
        this.model = model;
    }
    /**
    * validation date.
    */
    public boolean validDate (TaskInfo task) {
        if (task.getDate().before(new Date())) {
            return false;
        }
        return true;
    }

}//end ManagerController
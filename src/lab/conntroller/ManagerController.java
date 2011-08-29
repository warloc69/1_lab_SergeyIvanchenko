package lab.conntroller;
import lab.model.MannagerWrite;
import lab.*;
import java.util.Date;
import lab.exception.*;

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
    public void delTask(long id){        
        model.removeTask(id);    
    }

    /**
    * Edit task
    */
    public boolean editTask(long id, TaskInfo task){
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
            throw new BadTaskDateException("Date incorrect");
        }
		if (task.getExec() != null && !task.getExec().getName().equals(" ")) {
			String file = task.getExec().getPath();
			if(!file.regionMatches(true,file.length()-3,"exe",0,3)) {
				throw new BadTaskExecException("Chouse file incorrect");
			}
		}
        return true;
    }

}//end ManagerController
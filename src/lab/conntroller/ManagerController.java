package lab.conntroller;
import lab.model.MannagerWrite;
import lab.*;
import java.util.Date;
import lab.exception.*;

public class ManagerController implements ManagerControllerInterface {

    private MannagerWrite model;
	/**
    * validation task.
    */
    private void taskValidation (TaskInfo task) throws BadTaskException {
        if (task.getDate().before(new Date())) {
            throw new BadTaskException("Date incorrect");
        }
        if (task.getExec() != null && !task.getExec().getName().equals(" ")) {
            String file = task.getExec().getPath();
            if(!file.regionMatches(true,file.length()-3,"exe",0,3)) {
                throw new BadTaskException("Chouse file incorrect");
            }
        }
    }
	/**
	* constructor
	*/
    public ManagerController(){

    }
    /**
     * Add task
     */
    public void addTask(TaskInfo task) throws BadTaskException, DataAccessException{
        try { 
            taskValidation(task);
		} catch (BadTaskException e) {
            throw new BadTaskException(e.getMessage(),e.getCause());
        }
		model.addTask(task);                
        
    }

     /**
     * Remove task.
     * @param id remove task.
     */
    public void delTask(long id) throws DataAccessException {
        model.removeTask(id);
    }

    /**
    * Edit task
    */
    public void editTask(long id, TaskInfo task) throws BadTaskException, DataAccessException{
        try {
            taskValidation(task);
        } catch (BadTaskException e) {
            throw new BadTaskException(e.getMessage(),e.getCause());
        }      
		model.editTask(id,task);
        
    }
    /**
    * insert model into controller.
    */
    public void setModel(MannagerWrite model) {
        this.model = model;
    }
}//end ManagerController
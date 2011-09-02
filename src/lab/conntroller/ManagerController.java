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
		try {
            model.addTask(task);                
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage(),e.getCause());
        }
    }

     /**
     * Remove task.
     * @param id remove task.
     */
    public void delTask(long id) throws DataAccessException {        
        try {
            model.removeTask(id);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage(),e.getCause());
        }
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
		try {      
			model.editTask(id,task);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage(),e.getCause());
        }
    }
    /**
    * insert model into controller.
    */
    public void setModel(MannagerWrite model) {
        this.model = model;
    }
}//end ManagerController
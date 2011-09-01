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
    public boolean addTask(TaskInfo task) throws BadTaskException, BDException{
       try { 
			if (validTask(task)) {
				model.addTask(task);
				return true;
			} else {
				return false;
			}
        } catch (BadTaskException e) {
			throw new BadTaskException(e.getMessage());
		} catch (BDException e) {
			throw new BDException(e.getMessage());
		}
    }

     /**
     * Remove task.
     * @param id remove task.
     */
    public void delTask(long id) throws BDException {        
        try {
			model.removeTask(id);
		} catch (BDException e) {
			throw new BDException(e.getMessage());
		}
    }

    /**
    * Edit task
    */
    public boolean editTask(long id, TaskInfo task) throws BadTaskException, BDException{
        try {
			if (validTask(task)) {
				model.editTask(id,task);
				return true;
			} else {
				return false;
			}
		} catch (BadTaskException e) {
			throw new BadTaskException(e.getMessage());
		} catch (BDException e) {
			throw new BDException(e.getMessage());
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
    public boolean validTask (TaskInfo task) throws BadTaskException, BDException {
        if (task.getDate().before(new Date())) {
            throw new BadTaskException("Date incorrect");
        }
		if (task.getExec() != null && !task.getExec().getName().equals(" ")) {
			String file = task.getExec().getPath();
			if(!file.regionMatches(true,file.length()-3,"exe",0,3)) {
				throw new BadTaskException("Chouse file incorrect");
			}
		}
        return true;
    }

}//end ManagerController
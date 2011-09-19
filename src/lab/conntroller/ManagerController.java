package lab.conntroller;
import lab.model.MannagerWrite;
import lab.*;
import java.util.Date;
import lab.exception.*;
/**
*  Class creates object controlling change in the model.
*/
public class ManagerController implements ManagerControllerInterface {
    private MannagerWrite model;
    /**
    * validation task.
    * @param task reference on the validation task.
    * @throws BadTaskException if task is invalide.
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
        if (task.getName().length() == 0) {
            throw new BadTaskException("Name incorrect");
        }
    }
    /**
     * Add task
     * @throws DataAccessException if we can't have access to Data Base.
     * @throws BadTaskException if task is invalide.
     * @param task reference on the add task.
     */
    public void addTask(TaskInfo task) throws BadTaskException, DataAccessException{
        taskValidation(task);
        model.addTask(task);
    }
     /**
     * Remove task.
     * @param id remove task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void delTask(long id) throws DataAccessException {
        model.removeTask(id);
    }
    /**
    * Edit task
    * @throws DataAccessException if we can't have access to Data Base.
    * @throws BadTaskException if task is invalide.
    * @param task reference on the edit task.
    */
    public void editTask(long id, TaskInfo task) throws BadTaskException, DataAccessException{
        taskValidation(task);     
        model.editTask(id,task);        
    }
    /**
    * insert model into controller.
    * @param model reference of the model.
    */
    public void setModel(MannagerWrite model) {
        this.model = model;
    }
}//end ManagerController
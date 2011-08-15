package lab.model;
import lab.TaskInfo;
public interface MannagerWrite {

    /**
     * Remove task.
     * @param id remove task.
     */
    public void removeTask(int id);

    /**
     * Add task
     */
    public void addTask(TaskInfo taskinfo);    
    /**
    * Edit task
    */
    public void editTask(int id, TaskInfo task);
}
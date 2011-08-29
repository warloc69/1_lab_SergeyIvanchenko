package lab.model;
import java.util.Hashtable;
import lab.TaskInfo;
public interface ModelGetInf {
    /**
    *    Returns All tasks
    */
    public Hashtable<Long,TaskInfo> getAllTasks();
    /**
    *    Returns task.
    */
    public TaskInfo getTask(long id);
}
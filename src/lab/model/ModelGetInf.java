package lab.model;
import java.util.Hashtable;
import lab.TaskInfo;
public interface ModelGetInf {
    /**
    *    Returns All tasks
    */
    public Hashtable<Integer,TaskInfo> getAllTasks();
    /**
    *    Returns task.
    */
    public TaskInfo getTask(int id);
}
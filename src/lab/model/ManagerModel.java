package lab.model;
import java.util.*;
import lab.TaskInfo;
import lab.model.bridge.*;
import lab.view.*;
import java.io.*;
import lab.exception.*;
/**
 * Class Create model for the Task Manager
 */
public class ManagerModel implements  MannagerWrite, ModelGetInf, lab.model.observer.Observer {
    private Bridge sqlBridge;
    private ArrayList<lab.model.observer.Observable> lisener = null;
    private Hashtable<Long,TaskInfo> taskMap;
    /**
    *    It's private class generates id for the task.
    */
    private static class IDGenerator {
        static long current = System.currentTimeMillis();
        static public synchronized long get(){
            return current++;
        }
    } 
    /**
    * Load task from BD
    * @throws DataAccessException if we can't have access to Data Base.
    */
    private void loadTasks() throws DataAccessException {
            taskMap = sqlBridge.getAll();
    }
    /**
    * Constructor create model object.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public ManagerModel() throws DataAccessException {
        lisener = new ArrayList<lab.model.observer.Observable>();
        sqlBridge = new SQLiteBridge();        
        loadTasks();
    }
    /**
    * Add all observer end update information about tasks.
    */
    public void addObserver(lab.model.observer.Observable view) {
        lisener.add(view);
        view.notifyGetAll(this);
    }
    /**
    * Remove observer.
    */
    public void removeObserver(lab.model.observer.Observable view) {
        lisener.remove(view);
    }
    /**
     * Remove task.
     * @param id remove task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeTask(long id) throws DataAccessException{
        sqlBridge.removeTask(id);
        taskMap.remove(id);
        for (int i = 0; i <lisener.size(); i++ ) {
            lisener.get(i).notifyRemove(id);
        }
    }

    /**
     * Add task
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void addTask(TaskInfo task) throws DataAccessException{
        task.setID(IDGenerator.get());
        sqlBridge.addTask(task);
        taskMap.put(task.getID(),task);        
        for (int i = 0; i <lisener.size(); i++ ) {
            lisener.get(i).notifyAdd(task.getID());
        }
    }
    /**
    * Edit task
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public void editTask(long id, TaskInfo task) throws DataAccessException {
        sqlBridge.editTask(id,task);
        taskMap.put(id,task);
        for (int i = 0; i <lisener.size(); i++ ) {
            lisener.get(i).notifyEdit(id);
        }
    }
    /**
    *    Returns All tasks
    */
    @SuppressWarnings("unchecked")
    public Hashtable<Long,TaskInfo> getAllTasks() {
        return (Hashtable<Long,TaskInfo>) taskMap.clone();
    }
    /**
    *    Returns task.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public TaskInfo getTask(long id) throws DataAccessException{
        if (taskMap.containsKey(id)) {
            return taskMap.get(id);
        }
        return sqlBridge.getTask(id);
    }
    
}//end ManagerModel
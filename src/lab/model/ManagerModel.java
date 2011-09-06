package lab.model;
import java.util.*;
import lab.TaskInfo;
import lab.model.bridge.*;
import lab.view.*;
import java.io.*;
import lab.exception.*;

public class ManagerModel implements  MannagerWrite, ModelGetInf, lab.model.observer.Observer {
    private Bridge SQLBridge;
    ArrayList<lab.model.observer.Observable> lisener = null;
    private Hashtable<Long,TaskInfo> taskMap;
    private static class IDGenerator {
        static long current = System.currentTimeMillis();
        static public synchronized long get(){
            return current++;
        }
    } 
	/**
    * Load task from BD.
    */
    private void loadTasks() throws DataAccessException {
            taskMap = SQLBridge.getAll();
    }
    public ManagerModel() throws DataAccessException {
        lisener = new ArrayList<lab.model.observer.Observable>();
		SQLBridge = new SQLiteBridge();        
		loadTasks();
    }
    /**
    * Add all observer end update information about tasks.
    */
    public void addObserver(lab.model.observer.Observable view) {
        lisener.add(view);
        view.notifyGetAll(this);
    }
    public void removeObserver(lab.model.observer.Observable view) {
        lisener.remove(view);
    }
    /**
     * Remove task.
     * @param id remove task.
     */
    public void removeTask(long id) throws DataAccessException{
        SQLBridge.removeTask(id);
        taskMap.remove(id);
        for (int i = 0; i <lisener.size(); i++ ) {
            lisener.get(i).notifyRemove(id);
        }
    }

    /**
     * Add task
     */
    @SuppressWarnings("unchecked")
    public void addTask(TaskInfo task) throws DataAccessException{
        task.setID(IDGenerator.get());
        taskMap.put(task.getID(),task);
        SQLBridge.addTask(task);
        for (int i = 0; i <lisener.size(); i++ ) {
            lisener.get(i).notifyAdd(task.getID());
        }
    }
    /**
    * Edit task
    */
    public void editTask(long id, TaskInfo task) throws DataAccessException {
        SQLBridge.editTask(id,task);
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
    */
    public TaskInfo getTask(long id) throws DataAccessException{
        if (taskMap.containsKey(id)) {
			return taskMap.get(id);
		}
        return SQLBridge.getTask(id);
    }
    
}//end ManagerModel
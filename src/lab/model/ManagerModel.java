package lab.model;
import java.util.*;
import lab.TaskInfo;
import lab.model.bridge.*;
import lab.view.*;
import java.io.*;
import lab.exception.*;

public class ManagerModel implements  MannagerWrite, ModelGetInf {
    private Bridge SQLBridge;
	lab.model.observer.Observable lisener = null;
    private Hashtable<Long,TaskInfo> taskMap;
    private static class IDGenerator {
		static long current = System.currentTimeMillis();
		static public synchronized long get(){
			return current++;
		}
	} 

	public ManagerModel() {
        SQLBridge = SQLiteBridge.getInstance();
        loadTask();
    }
    /**
    * Add all observer end update information about tasks.
    */
    public void addObserver(lab.model.observer.Observable view) {
        lisener = view;
		lisener.notifyGetAll(this);
    }
    /**
    * Load task from BD.
    */
    @SuppressWarnings("unchecked")
    public void loadTask() {
        taskMap = SQLBridge.getAll();
    }

     /**
     * Remove task.
     * @param id remove task.
     */
    public void removeTask(long id){
        SQLBridge.removeTask(id);
        taskMap.remove(id);
        lisener.notifyRemove(id);
    }

    /**
     * Add task
     */
    @SuppressWarnings("unchecked")
    public void addTask(TaskInfo task){        
        task.setID(IDGenerator.get());
        taskMap.put(task.getID(),task);
		SQLBridge.addTask(task);	
        lisener.notifyAdd(task.getID());
    }
    /**
    * Edit task
    */
    public void editTask(long id, TaskInfo task) {
        SQLBridge.editTask(id,task);
        taskMap.put(id,task);
        lisener.notifyEdit(id);
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
    public TaskInfo getTask(long id) {
        return SQLBridge.getTask(id);
    }
    
}//end ManagerModel
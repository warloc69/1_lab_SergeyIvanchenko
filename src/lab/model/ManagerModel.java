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

	public ManagerModel() {
		lisener = new ArrayList<lab.model.observer.Observable>();
        SQLBridge = SQLiteBridge.getInstance();
        loadTask();
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
    * Load task from BD.
    */
    @SuppressWarnings("unchecked")
    public void loadTask() {
		try {
			taskMap = SQLBridge.getAll();
		} catch (BDException e) {
			new ExceptionView("1 Can't load task");
		}
    }

     /**
     * Remove task.
     * @param id remove task.
     */
    public void removeTask(long id) throws BDException{
        try {
			SQLBridge.removeTask(id);
			taskMap.remove(id);
			for (int i = 0; i <lisener.size(); i++ ) {
				lisener.get(i).notifyRemove(id);
			}
		} catch (BDException e) {
			throw new BDException(e.getMessage());
		}
    }

    /**
     * Add task
     */
    @SuppressWarnings("unchecked")
    public void addTask(TaskInfo task) throws BDException{        
        try {
			task.setID(IDGenerator.get());
			taskMap.put(task.getID(),task);
			SQLBridge.addTask(task);
			for (int i = 0; i <lisener.size(); i++ ) {
				lisener.get(i).notifyAdd(task.getID());
			}
		} catch (BDException e) {
			throw new BDException(e.getMessage());
		}
    }
    /**
    * Edit task
    */
    public void editTask(long id, TaskInfo task) throws BDException {
        try {
			SQLBridge.editTask(id,task);
			taskMap.put(id,task);
			for (int i = 0; i <lisener.size(); i++ ) {
				lisener.get(i).notifyEdit(id);
			}
		} catch (BDException e) {
			throw new BDException(e.getMessage());
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
    public TaskInfo getTask(long id) throws BDException{
        try {
			return SQLBridge.getTask(id);
		} catch (BDException e) {
			throw new BDException(e.getMessage());
		}
    }
    
}//end ManagerModel
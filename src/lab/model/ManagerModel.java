package lab.model;
import java.util.*;
import lab.TaskInfo;
import lab.bridge.*;
import lab.view.*;
import java.io.*;

public class ManagerModel extends Observable implements  MannagerWrite {
    private Bridge SQLBridge;
    private Hashtable taskMap;
    private int maxID;
    public ManagerModel() {
        SQLBridge = SQLiteBridge.getInstance();
        taskMap = new Hashtable();
        maxID = 0;
        loadTask();
    }
    /**
    * Add all observer end update information about tasks.
    */
    public void addObserver(Observer view) {
        super.addObserver(view);
        setChanged();
        notifyObservers(taskMap.clone());
    }
    /**
    * return maximal ID
    */
    public int getMaxID() {
        return maxID;
    }
    /**
    * Load task from BD.
    */
    @SuppressWarnings("unchecked")
    public void loadTask() {
        taskMap = SQLBridge.getAll();
        maxID = taskMap.size();
    }

     /**
     * Remove task.
     * @param id remove task.
     */
    public void removeTask(int id){
        SQLBridge.removeTask(id);
        taskMap.remove(id);
        setChanged();
        notifyObservers(taskMap.clone());
    }

    /**
     * Add task
     */
    @SuppressWarnings("unchecked")
    public void addTask(TaskInfo task){
        task.setID(getMaxID());
        taskMap.put(task.getID(),task);        
        SQLBridge.addTask(task);
        setChanged();
        notifyObservers(taskMap.clone());
        maxID++;
    }
    /**
    * Edit task
    */
    public void editTask(int id, TaskInfo task) {
        SQLBridge.editTask(id,task);
    }
}//end ManagerModel
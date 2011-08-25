package lab.model;
import java.util.*;
import lab.TaskInfo;
import lab.model.bridge.*;
import lab.view.*;
import java.io.*;

public class ManagerModel extends Observable implements  MannagerWrite, ModelGetInf {
    private Bridge SQLBridge;
    private Hashtable<Integer,TaskInfo> taskMap;
    private Stack<Integer> freeID = null;
    public ManagerModel() {
        SQLBridge = SQLiteBridge.getInstance();
        loadTask();
        getMaxID();
    }
    /**
    * Add all observer end update information about tasks.
    */
    public void addObserver(Observer view) {
        super.addObserver(view);
        setChanged();
        notifyObservers(this);
    }
    /**
    * return maximal ID
    */
    public int getMaxID() {
        if (freeID == null) {
            freeID = new Stack<Integer>();
            for (int i = 0; i < taskMap.size()+1; i++) {
                if(!taskMap.containsKey(i)) {
                    freeID.push(i);
                } else {
                }
            }
            return 0;
        }
        if (!freeID.empty()) {        
            int i =  freeID.pop();
            return i;
        } else {
            int i =  taskMap.size()+1;
            return i;
        }
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
    public void removeTask(int id){
        SQLBridge.removeTask(id);
        taskMap.remove(id);
        freeID.push(id);
        Long com = (ModelConst.removeCom << 31);
        com += id;
        setChanged();        
        notifyObservers(com);
    }

    /**
     * Add task
     */
    @SuppressWarnings("unchecked")
    public void addTask(TaskInfo task){        
        task.setID(getMaxID());
        taskMap.put(task.getID(),task);        
        SQLBridge.addTask(task);
        Long com = (ModelConst.addCom << 31);
        com += task.getID();
        setChanged();
        notifyObservers(com);
    }
    /**
    * Edit task
    */
    public void editTask(int id, TaskInfo task) {
        SQLBridge.editTask(id,task);
        taskMap.put(id,task);
        Long com = (ModelConst.editCom << 31);
        com += id;
        setChanged();
        notifyObservers(com);
    }
    /**
    *    Returns All tasks
    */
    @SuppressWarnings("unchecked")
    public Hashtable<Integer,TaskInfo> getAllTasks() {
        return (Hashtable<Integer,TaskInfo>) taskMap.clone();
    }
    /**
    *    Returns task.
    */
    public TaskInfo getTask(int id) {
        return SQLBridge.getTask(id);
    }
    
}//end ManagerModel
package lab;
import java.util.Date;
import java.io.*;
/**
 * @author serg
 * @version 1.0
 * @created 06-Сер-2011 14:05:01
 */
public class TaskInfoImpl implements TaskInfo {
    private long ID;
    private Date taskDate;
    private File taskDataExec;
    private String taskInfo;
    private String taskName;
    public TaskInfoImpl(){

    }
    /**
    * returns task ID.
    */
    public long getID() {
        return ID;
    }
   /**
     * returns task Date.
     * 
     */
    public Date getDate(){
        return taskDate;
    }

     /**
     * returns execute program.
     * 
     */
    public File getExec(){
        return taskDataExec;
    }
    /**
     * returns the information about task.
     * 
     */
    public String getInfo(){
        return taskInfo;
    }

    /**
     * returns the task name.
     * 
     */
    public String getName(){
        return taskName;
    }

     /**
     * Sets task Date.
     * 
     */
    public void setDate(Date date){
        taskDate = date;
    }

    /**
     * Sets task execute program.
     */
    public void setExec(File file){
        taskDataExec = file;
    }

    /**
     * 
     * Sets the information about the task
     */
    public void setInfo(String info){
        taskInfo = info;
    }

    /**
     * 
     * Sets the task name.
     */
    public void setName(String name){
        taskName = name;
    }
    /**
    * Sets task ID.
    */
    public void setID(long id) {
        ID = id;
    }
}//end TaskInfo
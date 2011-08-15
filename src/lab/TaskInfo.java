package lab;
import java.util.Date;
import java.io.*;
/**
 * @author serg
 * @version 1.0
 * @created 06-Сер-2011 14:05:01
 */
public class TaskInfo {
    private int ID;
    private Date taskDate;
    private File taskDataExec;
    private String taskInfo;
    private String taskName;
    public TaskInfo(){

    }
    public int getID() {
        return ID;
    }
    /**
     * 
     * 
     */
    public Date getDate(){
        return taskDate;
    }

    /**
     * 
     * 
     */
    public File getExec(){
        return taskDataExec;
    }

    /**
     * 
     * 
     */
    public String getInfo(){
        return taskInfo;
    }

    /**
     * 
     * 
     */
    public String getName(){
        return taskName;
    }

    /**
     * 
     * @param data
     */
    public void setDate(Date date){
        taskDate = date;
    }

    /**
     * 
     * @param file
     */
    public void setExec(File file){
        taskDataExec = file;
    }

    /**
     * 
     * @param info
     */
    public void setInfo(String info){
        taskInfo = info;
    }

    /**
     * 
     * @param name
     */
    public void setName(String name){
        taskName = name;
    }
    public void setID(int id) {
        ID = id;
    }
    public byte [] toBytes() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append("|");
        sb.append(getInfo());
        sb.append("|");
        sb.append(getDate().getTime());
        sb.append("|");
        sb.append(getExec().getPath());
        String s = sb.toString();
        return s.getBytes();
    }
}//end TaskInfo
package lab;
import java.util.Date;
import java.io.*;

public interface TaskInfo {
    /**
    * returns task ID.
    */
    public int getID(); 
    /**
     * returns task Date.
     * 
     */
    public Date getDate();

    /**
     * returns execute program.
     * 
     */
    public File getExec();

    /**
     * returns the information about task.
     * 
     */
    public String getInfo();

    /**
     * returns the task name.
     * 
     */
    public String getName();

    /**
     * Sets task Date.
     * 
     */
    public void setDate(Date date);

    /**
     * Sets task execute program.
     */
    public void setExec(File file);

    /**
     * 
     * Sets the information about the task
     */
    public void setInfo(String info);

    /**
     * 
     * Sets the task name.
     */
    public void setName(String name);
    /**
    * Sets task ID.
    */
    public void setID(int id) ;
}//end TaskInfo
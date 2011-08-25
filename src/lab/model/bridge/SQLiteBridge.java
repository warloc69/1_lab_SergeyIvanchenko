package lab.model.bridge;
import lab.*;
import java.io.File;
import com.almworks.sqlite4java.*;
import java.util.Date;
import java.util.*;
public class SQLiteBridge implements Bridge{
    private static final SQLiteBridge INSTANCE = new SQLiteBridge();
    SQLiteConnection db;
    SQLiteStatement st;
    SQLiteQueue queue = null;
    private SQLiteBridge() {
        try{
            db = new SQLiteConnection(new File("default.ndb"));
            db.open(true);
            queue = new SQLiteQueue(new File("default.ndb"));
            queue.start();
            queue.execute(new SQLiteJob<Object>() {
                protected Object job(SQLiteConnection connection) 
                        throws SQLiteException {
                    connection.exec("BEGIN TRANSACTION;CREATE TABLE IF NOT EXISTS tasks (id INTEGER, name TEXT, info TEXT, file TEXT, data INTEGER);COMMIT");
                    return null;
                }                
            });
        } catch (SQLiteException ex){
           
        }
    }
    /**
     * Add task
     */
    public void addTask(TaskInfo task){
        StringBuffer sb = new StringBuffer("BEGIN TRANSACTION;INSERT INTO tasks VALUES('");
        sb.append(task.getID());
        sb.append("','");
        sb.append(task.getName());
        sb.append("','");
        sb.append(task.getInfo());
        sb.append("','");
        if ( task.getExec() != null ) {
            sb.append(task.getExec().getPath());
        } else {
            sb.append("null");
        }
        sb.append("','");
        sb.append(task.getDate().getTime());
        sb.append("');COMMIT;");
        final String command = sb.toString();
        execs(command);
    }
    /**
    *    return SQLiteBridge object.
    */
    public static SQLiteBridge getInstance() {
        return INSTANCE;
    }
    /**
    * execute SQL commands
    */
    private boolean execs(final String str){
        queue.execute(new SQLiteJob<Object>() {
            protected Object job(SQLiteConnection connection) 
                    throws SQLiteException {
                connection.exec(str);
                return null;
            }
        });
        return true;
    }
     /**
     * Remove All task..
     */
    public void removeAll() {
        execs("BEGIN TRANSACTION;DELETE * FROM tasks;COMMIT;");
    }
     /**
     * Remove task.
     * @param id remove task.
     */
    public void removeTask(int id) {
        String command = "BEGIN TRANSACTION;DELETE FROM tasks WHERE id = " + id + ";COMMIT;";
        execs(command);
    }
    /**
    * returns rows count
    */
    public int getCountID() {
        return queue.execute(new SQLiteJob<Integer>() {
            protected Integer job(SQLiteConnection connection) throws SQLiteException {
                SQLiteStatement st = connection.prepare("SELECT COUNT(*) FROM tasks");
                try {
                    st.step();
                    return st.columnInt(0);
                } finally {
                    st.dispose();
                }
            }
        }).complete();
    }
    /**
    * Get task from file.
    */
    public TaskInfo getTask(final int id){
        return queue.execute(new SQLiteJob<TaskInfo>() {
            protected TaskInfo job(SQLiteConnection connection) throws SQLiteException {
                SQLiteStatement st = connection.prepare("SELECT * FROM tasks WHERE id = " + id);
                try {
                    if (!st.step()) {
                        st.dispose();
                        return null;
                    } else {
                    TaskInfo taskTemp = new TaskInfoImpl();
                        if (st.hasRow()) {
                            taskTemp.setID((Integer)st.columnValue(0));
                            taskTemp.setName((String)st.columnValue(1));
                            taskTemp.setInfo((String)st.columnValue(2));
                            String s = (String)st.columnValue(3);
                        if (s.equals("null")) {
                            taskTemp.setExec(null);
                        } else {
                            taskTemp.setExec(new File((String)st.columnValue(3)));
                        }
                        taskTemp.setDate(new Date((Long) st.columnValue(4)));
                        return taskTemp;
                        } else {
                            st.dispose();
                            return null;
                        }
                    }
                } finally {
                    st.dispose();
                }
            }
        }).complete();
    }
    /**
    * Load all tasks from file.
    */
    public Hashtable<Integer,TaskInfo> getAll(){
            final int columns = getCountID();
            return queue.execute(new SQLiteJob<Hashtable<Integer,TaskInfo>>() {
                protected Hashtable<Integer,TaskInfo> job(SQLiteConnection connection) throws SQLiteException {
                    SQLiteStatement st = connection.prepare("SELECT * FROM tasks");
                    try {
                        Hashtable<Integer,TaskInfo> h = new Hashtable<Integer,TaskInfo>();
                        for(int i = 0; i < columns; i++) {
                            if (!st.step()) {
                                st.dispose();
                                break;
                            } else {
                                TaskInfo taskTemp = new TaskInfoImpl();
                                if (st.hasRow()) {                                    
                                    taskTemp.setID((Integer)st.columnValue(0));
                                    taskTemp.setName((String)st.columnValue(1));
                                    taskTemp.setInfo((String)st.columnValue(2));
                                    String s = (String)st.columnValue(3);
                                if (s.equals("null")) {
                                    taskTemp.setExec(null);
                                } else {
                                    taskTemp.setExec(new File((String)st.columnValue(3)));
                                }
                                taskTemp.setDate(new Date((Long) st.columnValue(4)));
                                h.put(taskTemp.getID(),taskTemp);
                                } else {
                                    st.dispose();
                                    break;
                                }
                            }
                        }
                        return h;
                    } finally {
                        st.dispose();
                    }
                }
        }).complete();
        }
    /**
    * Edit task
    */
    public void editTask(int id, TaskInfo task){
        StringBuffer sb = new StringBuffer("BEGIN TRANSACTION;UPDATE tasks SET name='");
        sb.append(task.getName());
        sb.append("', info='");
        sb.append(task.getInfo());
        sb.append("', file='");
        if ( task.getExec() != null ) {
            sb.append(task.getExec().getPath());
        } else {
            sb.append("null");
        }
        sb.append("',data=");
        sb.append(task.getDate().getTime());
        sb.append(" WHERE id = "+id+";COMMIT;");
        String command = sb.toString();
        execs(command);
    }
}
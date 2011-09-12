package lab.model.bridge;
import lab.*;
import lab.model.*;
import java.io.File;
import com.almworks.sqlite4java.*;
import java.util.Date;
import java.util.*;
import lab.exception.*;
/**
* This class gives access to the Data Base
*/
public class SQLiteBridge implements Bridge{
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SQLiteBridge.class);
    private SQLiteQueue queue = null;
    /**
    * Chang string character number 39 (') for the adding into the Database.
    */
    private String escapeString(String str) {
        StringBuilder strNew = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == 39) {
                strNew.append("''");
            } else {
                strNew.append(ch);
            }
        }
        return strNew.toString();
    }
    /**
    * Creates a connection to an in-memory temporary database.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public SQLiteBridge() throws DataAccessException {
        queue = new SQLiteQueue(new File(ModelConst.nameBD));
        queue.start();
        SQLiteJob<Integer> job = queue.execute(new SQLiteJob<Integer>() {
            protected Integer job(SQLiteConnection connection) 
                throws SQLiteException {
                    connection.exec("BEGIN TRANSACTION;CREATE TABLE IF NOT EXISTS tasks (id LONG, name TEXT, info TEXT, file TEXT, data INTEGER);COMMIT");
                return -1;
            }                
        });
        if (job.complete() == null) {
            log.error(job.getError());
            throw new DataAccessException("1 DataBase create table error",job.getError());
        } else {
            if (log.isInfoEnabled()) {
                log.info("open DataBase, create or open table");
            }
        }
    }
    /**
     * Add task
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void addTask(TaskInfo task)throws DataAccessException {
        StringBuffer sb = new StringBuffer("BEGIN TRANSACTION;INSERT INTO tasks VALUES('");
        sb.append(task.getID());
        sb.append("','");
        sb.append(escapeString(task.getName()));
        sb.append("','");
        sb.append(escapeString(task.getInfo()));
        sb.append("','");
        if ( task.getExec() != null ) {
            sb.append(escapeString(task.getExec().getPath()));
        } else {
            sb.append("null");
        }
        sb.append("','");
        sb.append(task.getDate().getTime());
        sb.append("');COMMIT;");
        final String command = sb.toString();
        SQLiteJob<Integer> job = queue.execute(new SQLiteJob<Integer>() {
            protected Integer job(SQLiteConnection connection) 
                throws SQLiteException {
                    connection.exec(command);
                return -1;
            }
        });
        if (job.complete() == null) {
            log.error(job.getError());
            throw new DataAccessException("Add task Error",job.getError());
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, add task");
        }
    }
     /**
     * Remove All task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeAll() throws DataAccessException{
        final String command  = "BEGIN TRANSACTION;DELETE * FROM tasks;COMMIT;";
        SQLiteJob<Integer> job = queue.execute(new SQLiteJob<Integer>() {
            protected Integer job(SQLiteConnection connection) 
                throws SQLiteException {
                    connection.exec(command);
                return -1;
            }
        });
        if (job.complete() == null) {
            log.error(job.getError());
            throw new DataAccessException("Remove all tasks Error",job.getError());
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, remove all task");
        }
    }
     /**
     * Remove task.
     * @param id remove task.
     * @throws DataAccessException if we can't have access to Data Base.
     */
    public void removeTask(long id) throws DataAccessException{
        final String command = "BEGIN TRANSACTION;DELETE FROM tasks WHERE id = " + id + ";COMMIT;";
        SQLiteJob<Integer> job = queue.execute(new SQLiteJob<Integer>() {
            protected Integer job(SQLiteConnection connection) 
                throws SQLiteException {
                    connection.exec(command);                    
                return -1;
            }
        });
        if (job.complete() == null) {
            log.error(job.getError());
            throw new DataAccessException("Remove task Error",job.getError());            
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, remove task");
        }
    }
    /**
    * Get task from file.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public TaskInfo getTask(final long id) throws DataAccessException{
        SQLiteJob<TaskInfo> job = queue.execute(new SQLiteJob<TaskInfo>() {
            protected TaskInfo job(SQLiteConnection connection) throws SQLiteException {
                SQLiteStatement st = connection.prepare("SELECT * FROM tasks WHERE id = " + id);
                try {
                    if (!st.step()) {
                        st.dispose();
                        return null;
                    } else {
                        TaskInfo taskTemp = new TaskInfoImpl();
                        if (st.hasRow()) {
                            taskTemp.setID((Long)st.columnValue(0));
                            taskTemp.setName((String)st.columnValue(1));
                            taskTemp.setInfo((String)st.columnValue(2));
                            String s = (String)st.columnValue(3);
                            if ("null".equals(s)) {
                                taskTemp.setExec(null);
                            } else {
                                taskTemp.setExec(new File(s));
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
        });
        if (job.complete() == null) {
            log.error(job.getError());
            throw new DataAccessException("Get Task Error",job.getError());
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, get task");
        }
        return job.complete();
    }
    /**
    * Load all tasks from file.
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public Hashtable<Long,TaskInfo> getAll() throws DataAccessException {
            SQLiteJob<Hashtable<Long,TaskInfo>> job =  queue.execute(new SQLiteJob<Hashtable<Long,TaskInfo>>() {
                protected Hashtable<Long,TaskInfo> job(SQLiteConnection connection) throws SQLiteException {
                    SQLiteStatement st = connection.prepare("SELECT * FROM tasks");
                    try {
                        Hashtable<Long,TaskInfo> h = new Hashtable<Long,TaskInfo>();
                        while (true) { 
                            if (!st.step()) {
                                st.dispose();
                                break;
                            } else {
                                TaskInfo taskTemp = new TaskInfoImpl();
                                if (st.hasRow()) {                                    
                                    taskTemp.setID((Long)st.columnValue(0));
                                    taskTemp.setName((String)st.columnValue(1));
                                    taskTemp.setInfo((String)st.columnValue(2));
                                    String s = (String)st.columnValue(3);
                                    if ("null".equals(s)) {
                                        taskTemp.setExec(null);
                                    } else {
                                        taskTemp.setExec(new File(s));
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
        });
        if (job.complete() == null) {
            log.error(job.getError());
            throw new DataAccessException("Get Task Error",job.getError());
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, get all tasks");
        }
        return job.complete();
    }
    /**
    * Edit task
    * @throws DataAccessException if we can't have access to Data Base.
    */
    public void editTask(long id, TaskInfo task) throws DataAccessException {
        StringBuffer sb = new StringBuffer("BEGIN TRANSACTION;UPDATE tasks SET name='");
        sb.append(escapeString(task.getName()));
        sb.append("', info='");
        sb.append(escapeString(task.getInfo()));
        sb.append("', file='");
        if ( task.getExec() != null ) {
            sb.append(escapeString(task.getExec().getPath()));
        } else {
            sb.append("null");
        }
        sb.append("',data=");
        sb.append(task.getDate().getTime());
        sb.append(" WHERE id = "+ id +";COMMIT;");
        final String command = sb.toString();
        SQLiteJob<Integer> job = queue.execute(new SQLiteJob<Integer>() {
            protected Integer job(SQLiteConnection connection) 
                throws SQLiteException {
                    connection.exec(command);
                return -1;
            }
        });
        if (job.complete() == null ) {
            log.error(job.getError());
            throw new DataAccessException("Edit task Error",job.getError());
        }
        if (log.isInfoEnabled()) {
            log.info("DataBase, edit task");
        }
    }
}
package lab.model.bridge;
import lab.*;
import java.io.File;
import com.almworks.sqlite4java.*;
import java.util.Date;
import java.util.*;
import lab.exception.*;
public class SQLiteBridge implements Bridge{
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SQLiteBridge.class);
    private static final SQLiteBridge INSTANCE = new SQLiteBridge();
    SQLiteQueue queue = null;
    private SQLiteBridge() {
        queue = new SQLiteQueue(new File("default.ndb"));
        queue.start();
        SQLiteJob<Integer> job = queue.execute(new SQLiteJob<Integer>() {
            protected Integer job(SQLiteConnection connection) 
                throws SQLiteException {
                try {
					connection.exec("BEGIN TRANSACTION;CREATE TABLE IF NOT EXISTS tasks (id LONG, name TEXT, info TEXT, file TEXT, data INTEGER);COMMIT");
					
				} catch (SQLiteException e) {
					return 0;
				}
				return -1;
            }                
        });
		if (job.complete() == 0) {
			new ExceptionView("1 DataBase create table error");
		} else {
			log.info("open DataBase, create or open table");
		}
    }
    /**
     * Add task
     */
    public void addTask(TaskInfo task)throws BDException {
        StringBuffer sb = new StringBuffer("BEGIN TRANSACTION;INSERT INTO tasks VALUES('");
        sb.append(task.getID());
        sb.append("','");
        sb.append(stringChanger(task.getName()));
        sb.append("','");
        sb.append(stringChanger(task.getInfo()));
        sb.append("','");
        if ( task.getExec() != null ) {
            sb.append(stringChanger(task.getExec().getPath()));
        } else {
            sb.append("null");
        }
        sb.append("','");
        sb.append(task.getDate().getTime());
        sb.append("');COMMIT;");
        final String command = sb.toString();
		SQLiteJob<Integer> job = queue.execute(new SQLiteJob<Integer>() {
			protected Integer job(SQLiteConnection connection) 
				throws SQLiteException, BDException {
				try {
					connection.exec(command);
					log.info("DataBase, add task");
				} catch (SQLiteException e) {
					return 0;
				}
				return -1;
			}
		});
		if (job.complete() == 0) {
			throw new BDException("Add task Error");
		}
    }	
    /**
    *    return SQLiteBridge object.
    */
    public static SQLiteBridge getInstance() {
        return INSTANCE;
    }
     /**
     * Remove All task..
     */
    public void removeAll() throws BDException{
		final String command  = "BEGIN TRANSACTION;DELETE * FROM tasks;COMMIT;";
		SQLiteJob<Integer> job = queue.execute(new SQLiteJob<Integer>() {
			protected Integer job(SQLiteConnection connection) 
				throws SQLiteException {
				try {
					connection.exec(command);
					log.info("DataBase, remove all task");
				} catch (SQLiteException e) {
					return 0;
				}
				return -1;
			}
		});
		if (job.complete() == 0) {
			throw new BDException("Remove all tasks Error");
		}
    }
     /**
     * Remove task.
     * @param id remove task.
     */
    public void removeTask(long id) throws BDException{
        final String command = "BEGIN TRANSACTION;DELETE FROM tasks WHERE id = " + id + ";COMMIT;";
        SQLiteJob<Integer> job = queue.execute(new SQLiteJob<Integer>() {
			protected Integer job(SQLiteConnection connection) 
				throws SQLiteException {
				try {
					connection.exec(command);
					log.info("DataBase, remove task");
				} catch (SQLiteException e) {
					return 0;
				}
				return -1;
			}
		});
		if (job.complete() == 0) {
			throw new BDException("Remove task Error");			
		}
    }
    /**
    * returns rows count
    */
    public long getCountID() throws BDException{
         SQLiteJob<Long> job = queue.execute(new SQLiteJob<Long>() {
            protected Long job(SQLiteConnection connection) throws SQLiteException {
                SQLiteStatement st = connection.prepare("SELECT COUNT(*) FROM tasks");
                try {
                    st.step();
					log.info("DataBase, get count tasks");
                    return st.columnLong(0);
				} catch (SQLiteException e) {
					return -1L;
                } finally {
                    st.dispose();
                }
            }
        });
		if (job.complete() == -1L) {
			throw new BDException("Get count id Error");
		}
		return job.complete();
    }
    /**
    * Get task from file.
    */
    public TaskInfo getTask(final long id) throws BDException{
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
                        if (s.equals("null")) {
                            taskTemp.setExec(null);
                        } else {
                            taskTemp.setExec(new File((String)st.columnValue(3)));
                        }
                        taskTemp.setDate(new Date((Long) st.columnValue(4)));
						log.info("DataBase, get task");
                        return taskTemp;
                        } else {
                            st.dispose();
                            return null;
                        }
                    }
				} catch (SQLiteException e) {
					return null;
                } finally {
                    st.dispose();
                }
            }
        });
		if (job.complete() == null) {
			throw new BDException("Get Task Error");
		}
		return job.complete();
    }
    /**
    * Load all tasks from file.
    */
    public Hashtable<Long,TaskInfo> getAll() throws BDException {
            final long columns = getCountID();
            SQLiteJob<Hashtable<Long,TaskInfo>> job =  queue.execute(new SQLiteJob<Hashtable<Long,TaskInfo>>() {
                protected Hashtable<Long,TaskInfo> job(SQLiteConnection connection) throws SQLiteException {
                    SQLiteStatement st = connection.prepare("SELECT * FROM tasks");
                    try {
                        Hashtable<Long,TaskInfo> h = new Hashtable<Long,TaskInfo>();
                        for(int i = 0; i < columns; i++) {
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
						log.info("DataBase, get all tasks");
                        return h;
					} catch (SQLiteException e) {
						return null;
                    } finally {
                        st.dispose();
                    }
                }
        });
		if (job.complete() == null) {
			throw new BDException("Get Task Error");
		}
		return job.complete();
    }
    /**
    * Edit task
    */
    public void editTask(long id, TaskInfo task) throws BDException {
        StringBuffer sb = new StringBuffer("BEGIN TRANSACTION;UPDATE tasks SET name='");
        sb.append(stringChanger(task.getName()));
        sb.append("', info='");
        sb.append(stringChanger(task.getInfo()));
        sb.append("', file='");
        if ( task.getExec() != null ) {
            sb.append(stringChanger(task.getExec().getPath()));
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
				try {
					connection.exec(command);
					log.info("DataBase, edit task");
				} catch (SQLiteException e) {
					return 0;
				}
				return -1;
			}
		});
		if (job.complete() == 0 ) {
			throw new BDException("Edit task Error");
		}
    }
	/**
	* Chang string character number 39 (') for the adding into the Database.
	*/
	public String stringChanger(String str) {
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
}
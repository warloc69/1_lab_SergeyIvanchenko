package lab.model.bridge;
import lab.*;
import java.io.File;
import com.almworks.sqlite4java.*;
import java.util.Date;
import java.util.*;
import lab.exception.*;
public class SQLiteBridge implements Bridge{
    private static final SQLiteBridge INSTANCE = new SQLiteBridge();
    SQLiteQueue queue = null;
    private SQLiteBridge() {
        queue = new SQLiteQueue(new File("default.ndb"));
        queue.start();
        queue.execute(new SQLiteJob<Object>() {
            protected Object job(SQLiteConnection connection) 
                throws SQLiteException {
                try {
					connection.exec("BEGIN TRANSACTION;CREATE TABLE IF NOT EXISTS tasks (id LONG, name TEXT, info TEXT, file TEXT, data INTEGER);COMMIT");
                } catch (SQLiteException e) {
					throw new BDException("Table Create Error",getError());
				}
				return null;
            }                
        });
    }
    /**
     * Add task
     */
    public void addTask(TaskInfo task){
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
		queue.execute(new SQLiteJob<Object>() {
			protected Object job(SQLiteConnection connection) 
				throws SQLiteException, BDException {
				try {
					connection.exec(command);
				} catch (SQLiteException e) {
					throw new BDException("Add task Error",getError());
				}
				return null;
			}
		});
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
    public void removeAll() {
		final String command  = "BEGIN TRANSACTION;DELETE * FROM tasks;COMMIT;";
		queue.execute(new SQLiteJob<Object>() {
			protected Object job(SQLiteConnection connection) 
				throws SQLiteException {
				try {
					connection.exec(command);
				} catch (SQLiteException e) {
					throw new BDException("Remove all tasks Error",getError());
				}
				return null;
			}
		});
    }
     /**
     * Remove task.
     * @param id remove task.
     */
    public void removeTask(long id) {
        final String command = "BEGIN TRANSACTION;DELETE FROM tasks WHERE id = " + id + ";COMMIT;";
        queue.execute(new SQLiteJob<Object>() {
			protected Object job(SQLiteConnection connection) 
				throws SQLiteException {
				try {
					connection.exec(command);
				} catch (SQLiteException e) {
					throw new BDException("Remove task Error",getError());
				}
				return null;
			}
		});
    }
    /**
    * returns rows count
    */
    public long getCountID() {
        return queue.execute(new SQLiteJob<Long>() {
            protected Long job(SQLiteConnection connection) throws SQLiteException {
                SQLiteStatement st = connection.prepare("SELECT COUNT(*) FROM tasks");
                try {
                    st.step();
                    return st.columnLong(0);
				} catch (SQLiteException e) {
					throw new BDException("Get count id Error",getError());
                } finally {
                    st.dispose();
                }
            }
        }).complete();
    }
    /**
    * Get task from file.
    */
    public TaskInfo getTask(final long id){
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
                        return taskTemp;
                        } else {
                            st.dispose();
                            return null;
                        }
                    }
				} catch (SQLiteException e) {
					throw new BDException("Get Task Error",getError());
                } finally {
                    st.dispose();
                }
            }
        }).complete();
    }
    /**
    * Load all tasks from file.
    */
    public Hashtable<Long,TaskInfo> getAll(){
            final long columns = getCountID();
            return queue.execute(new SQLiteJob<Hashtable<Long,TaskInfo>>() {
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
                        return h;
					} catch (SQLiteException e) {
						throw new BDException("Get Tasks Error",getError());
                    } finally {
                        st.dispose();
                    }
                }
        }).complete();
    }
    /**
    * Edit task
    */
    public void editTask(long id, TaskInfo task){
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
        sb.append(" WHERE id = "+id+";COMMIT;");
        final String command = sb.toString();
        queue.execute(new SQLiteJob<Object>() {
			protected Object job(SQLiteConnection connection) 
				throws SQLiteException {
				try {
					connection.exec(command);
				} catch (SQLiteException e) {
					throw new BDException("Edit task Error",getError());
				}
				return null;
			}
		});
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
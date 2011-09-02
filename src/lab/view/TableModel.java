package lab.view;
import java.util.*;
import lab.TaskInfo;
import javax.swing.table.*;
/**
*    This class create TableModel.
*/
public class TableModel extends AbstractTableModel {
    ArrayList<TaskInfo> info = null; 
    public static final long serialVersionUID = 213123123123l;
    public int total = 0;
    public int today = 0;
    public int tomorrow = 0;
    public int week = 0;
    public TableModel(Hashtable<Long,TaskInfo> table) {
        info = new ArrayList<TaskInfo>(table.values());
        sort();
        recount();
    }
    /**
    *    This method sort table.
    */
    public void sort() {        
        for (int j = 0; j < info.size(); j++)
            for (int i = 0; i < info.size(); i++) {
                if (info.get(j).getDate().getTime() < info.get(i).getDate().getTime()) {
                   TaskInfo temp = info.get(j);
                   info.set(j, info.get(i));
                   info.set(i, temp);
                }
            }
    }
    /**
    * Returns the row's count.
    */
    public int getRowCount() {
        total = info.size();
        return total;
    }
    /**
    * Returns the column's count.
    */
    public int getColumnCount() {
        return 4;
    }
    /**
    * Returns the table's value.
    */
    public Object getValueAt(int r, int c) {
        TaskInfo t = info.get(r);
        switch (c) {
            case 0: {
                return t.getName();
            }
            case 1: {
                return t.getInfo();
            }
            case 2: {
                return t.getDate().toString();
            }
            case 3: {
                if (t.getExec() != null) {
                    return t.getExec().getName();    
                } else {
                    return " ";
                }
            } 
            case 4: {
                return t.getID();
            }
        }
        return "";
    }
    /**
    * Returns the column's name.
    */
    public String getColumnName(int c) {
        switch (c) {
            case 0: {
                return "Name";
            }
            case 1: {
                return "Information";
            }
            case 2: {
                return "Date";
            }
            case 3: {
                return "Executable program";
            }
            case 4: {
                return "ID";
            }
        }
        return "";            
    }
    /**
    * Disabled row edit.
    */
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    /**
    * Returns taskID.
    */
    public long getID(int r) {
        return info.get(r).getID();
    }
    /**
    * add task to the table.
    */
    public void addTask(TaskInfo t) {
        Calendar cal1 = Calendar.getInstance();        
        cal1.setTime(t.getDate());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date());
        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
            if (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)) {
                today++;            
            }
            if ((cal1.get(Calendar.DAY_OF_MONTH) - cal2.get(Calendar.DAY_OF_MONTH)) == 1) {
                tomorrow++;            
            }
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                week++;            
            }
        }
        total++;
        for (int i = 0; i < info.size(); i++) {
            if (t.getDate().getTime() < info.get(i).getDate().getTime()) {
                info.add(i,t);
                return;
                }
            }
        info.add(t);
    }
    /**
    * edit the table rows.
    */
    public void editTask(int id, TaskInfo t) {
            info.set(id,t);
            recount();
    }
	/**
	*return task;
	*/
	public TaskInfo get(int r) {
		return info.get(r);
	}
    /**
    * Returns the selected row.
    */
    public int getSelectedRow (TaskInfo t) {
        for (int i = 0; i < info.size() ; i++) {
            if( info.get(i).getID() == t.getID()) {
                return i;
            }
        }
        return -1;
    }
    /**
    * Remove the task from table.
    */
    public void removeTask(int id) {
        Calendar cal1 = Calendar.getInstance();        
        cal1.setTime(info.get(id).getDate());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date());
        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
            if (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)) {
                today--;            
            }
            if ((cal1.get(Calendar.DAY_OF_MONTH) - cal2.get(Calendar.DAY_OF_MONTH)) == 1) {
                tomorrow--;            
            }
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                week--;            
            }
        }
        total--;
        info.remove(id);
    }
    /**
    *    change count task
    */
    public void recount() {
        today = 0;
        tomorrow = 0;
        week = 0;
        total = 0;
         for (int i = 0; i < info.size() ; i++) {
            TaskInfo t = info.get(i);
            Calendar cal1 = Calendar.getInstance();        
            cal1.setTime(t.getDate());
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(new Date());
            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                if (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)) {
                    today++;            
                }
                if ((cal1.get(Calendar.DAY_OF_MONTH) - cal2.get(Calendar.DAY_OF_MONTH)) == 1) {
                    tomorrow++;            
                }
                if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                    week++;            
                }
            }
            total++;
        }
    }

}
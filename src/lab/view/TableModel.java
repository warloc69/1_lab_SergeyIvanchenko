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
    public TableModel(Hashtable<Long,TaskInfo> table) {
        info = new ArrayList<TaskInfo>(table.values());
    }
    /**
    * Returns the row's count.
    */
    public int getRowCount() {
        return info.size();
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
        info.add(t);
    }
    /**
    * edit the table rows.
    */
    public void editTask(int id, TaskInfo t) {
            info.set(id,t);
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
        info.remove(id);        
    }

}
package lab.view;
import lab.conntroller.ManagerControllerInterface;
import lab.*;
import lab.model.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.text.*;
/**
 * Create user Interface
 */
public class ManagerView extends JFrame implements Observer, Runnable {
    public static final long serialVersionUID = 123312332l;
    private Integer id1 =-1;
    private ManagerControllerInterface controller;
    private Container pane = null;
    private Object obj1 = null;
    private int loadFirstView = 0;
    JTable table = null;
    JScrollPane scr = null;
    Object[][] taskList = null;
    Hashtable<Integer,TaskInfo> tasks = null;
    Thread thread = null;
    private class ExeFilter extends javax.swing.filechooser.FileFilter {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".exe") ||
                    f.isDirectory();
            }
            public String getDescription() {
                return "Program";
            }
        }
    public ManagerView(){    
        pane = getContentPane();
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setTitle("Task Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,600);
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }
    /**
    *    Create new trhread look in all task, 
    *     and chooses the task for execution. 
    */
    public void run() {
        while (thread != null) {    
            try {
                thread.sleep(1000);
            } catch (InterruptedException e) {}
				if (tasks != null) {
					Collection<TaskInfo> col = tasks.values();
					Iterator<TaskInfo> it = col.iterator();
					for (int i = 0; i < tasks.size(); i++) {
						if (!it.hasNext()) {
							break;
						}
						TaskInfo t =  it.next();
						if (t.getDate().before(new Date())) {
							viewMassage(t);
							controller.delTask(t.getID());
						}
					}
				}
        }
    }
    /**
    *    Add controller into view.
    */
    public void setController(ManagerControllerInterface controller) {
        this.controller = controller;
    }
    /**
    *    Load all swing element.
    */
    public void loadView(){
        if (loadFirstView != 1) {
            loadFirstView = 1;
            //------- Button Add Task
            ImageIcon add = new ImageIcon("img\\add.png");
           final JButton bAddtask = new JButton(add);      
            bAddtask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        viewAddTask(0);
                    }
                }
            );
            pane.add(bAddtask);
            //------- Button Remove Task
            ImageIcon remove = new ImageIcon("img\\remove.png");
            JButton bRemovetask = new JButton(remove);
            bRemovetask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        int[] i = table.getSelectedRows();
                        Integer id = (Integer)taskList[i[0]][4];
                        controller.delTask(id);
                    }
                }
            );
            pane.add(bRemovetask);
            //------ Button Edit Task
            ImageIcon edit = new ImageIcon("img\\edit.png");
            JButton bEdittask = new JButton(edit);
            bEdittask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        viewEditTask();
                    }
                }
            );
            pane.add(bEdittask);
            //------ Button View Task
            ImageIcon view = new ImageIcon("img\\view.png");
            JButton bViewtask = new JButton(view);
            bViewtask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        viewViewTask();
                    }
                }
            );
            addComponentListener( 
                new ComponentAdapter() {
                    public void componentResized(ComponentEvent e) {
                        update(null,obj1);
                    }
                }
            );
            pane.add(bViewtask);
            setVisible(true);
        }
    }

    /**
     * Update informatio about tasks 
     * @param obj keep information about tasks
     */
    @SuppressWarnings("unchecked")
    public void update(Observable obs, Object obj){
        obj1 = obj;
        if (scr != null) {
            pane.remove(scr);
            scr = null;
            table = null;
        }
        tasks = (Hashtable<Integer,TaskInfo>) obj;
        Collection<TaskInfo> col = tasks.values();
        Iterator<TaskInfo> it = col.iterator();
        int size = tasks.size();
        taskList = new Object[size][5];
        for (int i = 0; i < tasks.size(); i++) {
            if (!it.hasNext()) {
                break;
            }
            TaskInfo t = it.next();
            taskList[i][0] = t.getName();
            taskList[i][1] = t.getInfo();
            taskList[i][2] = t.getDate().toString();
            if (t.getExec() != null) {
                taskList[i][3] = t.getExec().getName();    
            } else {
                taskList[i][3] = " ";
            }
            taskList[i][4] = t.getID();
        }       
        String [] columns = { "Task name", "Information", "Date", "Run Program"};
        //------- Create JTable
            table = new JTable(taskList, columns);
            scr = new JScrollPane(table);
            Dimension d = getSize();
            scr.setBounds(0,60,d.width,d.height);
            pane.add(scr);
        
        loadView();
    }
    /**
    *    Show add / edit / view task dialog.
    */
    public void viewAddTask(final int command){
        int W = Toolkit.getDefaultToolkit().getScreenSize().width;
    int H = Toolkit.getDefaultToolkit().getScreenSize().height-50;  
        final JDialog f = new JDialog(this, true);
        f.setLayout(null);
        f.setSize(W/2,H/2);
        f.setBounds(W/4,H/4,W/2,H/2);
        f.addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    f.dispose();
                    loadView();
                }
            }
        );
        //----- JLabel name
        final JLabel lname = new JLabel("Name:");
        lname.setBounds(10,15,50,30);
        f.add(lname);
        //----- JTextField
        final JTextField tname = new JTextField();
        tname.setBounds(65,15,(W/4-65)*2,30);
        f.add(tname);
        //----- JLabel Info
        final JLabel linfo = new JLabel("Info:");
        linfo.setBounds(10,60,50,30);
        f.add(linfo);
        //----- JTextArea Info
        final JTextArea tinfo = new JTextArea();
        tinfo.setBounds(65,60,(W/4-65)*2,90);
        f.add(tinfo);
        //----- Date
		final JSpinner dateChooser = new JSpinner(new SpinnerDateModel());
		dateChooser.setBounds(65,160,160,30);
        f.add(dateChooser);
        //----- JLabel file
        final JLabel lFile = new JLabel("Program:");
        lFile.setBounds(125,H/2-105,(W/4-65)*2,30);    
        f.add(lFile);
        //----- JChoose Execute file
        final JFileChooser exefile = new JFileChooser();   
        exefile.setFileFilter(new ExeFilter());
        JButton bexefile = new JButton("Run program");
        bexefile.setBounds(10,H/2-105,110,30);    
        bexefile.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                exefile.showOpenDialog(f);
                lFile.setText(exefile.getSelectedFile().getPath());
            }
        });
        f.add(bexefile);
        //----- Button Save
        JButton save = null;
        switch (command) {
            case 0: { 
                save = new JButton("Save");
                break;
            }
            case 1: {
                save = new JButton("Save");
                int[] i = table.getSelectedRows();
                id1 = (Integer) taskList[i[0]][4];
                TaskInfo t1 =  tasks.get(id1);
				dateChooser.setValue(t1.getDate());
                tname.setText(t1.getName());
                tinfo.setText(t1.getInfo());
                if (t1.getExec() != null) {
                    lFile.setText(t1.getExec().getPath());
                    exefile.setSelectedFile(t1.getExec());
                }
                break;
            }
            case 2: {
                save = new JButton("Ok");
                int[] i = table.getSelectedRows();
                id1 = (Integer) taskList[i[0]][4];
                TaskInfo t1 =  tasks.get(id1);
				dateChooser.setValue(t1.getDate());
                tname.setText(t1.getName());
                tinfo.setText(t1.getInfo());
                if (t1.getExec() != null) {
                    lFile.setText(t1.getExec().getPath());
                    exefile.setSelectedFile(t1.getExec());
                }
                break;
            }
        }
        save.setBounds(10,H/2-70,100,30);        
        save.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (command == 2) {
                    f.dispose();
                    return;
                }
                TaskInfo ts = new TaskInfoImpl();
                ts.setName(tname.getText());
                ts.setInfo(tinfo.getText());
                ts.setDate((Date)dateChooser.getValue());
                if (exefile.getSelectedFile() != null) {
                    ts.setExec(exefile.getSelectedFile());
                } else {
                    ts.setExec(new File(" "));
                }
                if (command == 1) {
                    controller.editTask(id1,ts);
                } else {
                    controller.addTask(ts);
                }
                f.dispose();
            }
        });
        f.add(save);
        //----- Button Cancel
        JButton cancel = new JButton("Cancel");
        cancel.setBounds(115,H/2-70,100,30);    
        cancel.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                f.dispose();
            }            
        });
        f.add(cancel);    
        f.setResizable(false);
        if (command == 2) {
            tname.setEnabled(false);
            tinfo.setEnabled(false);
            f.remove(bexefile);
        }
        f.setVisible(true);
    }

    /**
     *    Show task Edit dialog.
     */
    public void viewEditTask(){
        viewAddTask(1);
    }

    /**
     *    Show task Edit dialog.
     */
    public void viewViewTask(){
        viewAddTask(2);
    }
    /**
    *    Show execute dialog.
    */
    public void viewMassage(TaskInfo ts) {
    int W = Toolkit.getDefaultToolkit().getScreenSize().width;
    int H = Toolkit.getDefaultToolkit().getScreenSize().height-50;    
        if (ts.getExec() != null && !ts.getExec().getName().equals(" ")){
            Runtime r = Runtime.getRuntime();
            try {
                r.exec(ts.getExec().getPath());
            } catch (IOException e) {}
        }
        final JDialog msg = new JDialog(this, true);
        msg.setLayout(null);
        msg.setSize(W/2,H/2);
        msg.setBounds(W/4,H/4,W/2,H/2);
                msg.addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    msg.dispose();
                    loadView();
                }
            }
        );
        //----- JLabel name
        final JLabel lname = new JLabel("Name:");
        lname.setBounds(10,15,50,30);
        msg.add(lname);
        //----- JTextField
        final JTextField tname = new JTextField();
        tname.setBounds(65,15,(W/4-65)*2,30);
        tname.setText(ts.getName());
        tname.setEnabled(false);
        msg.add(tname);
        //----- JLabel Info
        final JLabel linfo = new JLabel("Info:");
        linfo.setBounds(10,60,50,30);
        msg.add(linfo);
        //----- JTextArea Info
        final JTextArea tinfo = new JTextArea();
        tinfo.setBounds(65,60,(W/4-65)*2,90);
        tinfo.setText(ts.getInfo());
        tinfo.setEnabled(false);
        msg.add(tinfo);
        JButton cancel = new JButton("Ok");
        cancel.setBounds(115,H/2-70,100,30);    
        cancel.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                msg.dispose();
            }
        });
        msg.add(cancel);
        msg.setVisible(true);
    }
}//end ManagerView
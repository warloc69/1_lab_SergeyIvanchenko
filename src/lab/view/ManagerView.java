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
    private int q = 0;
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
                Collection col = tasks.values();
                Iterator it = col.iterator();
                for (int i = 0; i < tasks.size(); i++) {
                    if (!it.hasNext()) {
                        break;
                    }
                    TaskInfo t = (TaskInfo) it.next();
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
        if (q != 1) {
            q = 1;
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
    @SuppressWarnings("fallthrough")
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
        //----- JLabel Hour
        final JLabel lhour = new JLabel("Hour");
        lhour.setBounds(135,150,30,30);
        f.add(lhour);
        //----- JTextField Hour
        final JTextField thour = new JTextField();
        thour.setBounds(135,180,30,30);
        f.add(thour);
        //----- JLabel Minits
        final JLabel lMinits = new JLabel("Minits");
        lMinits.setBounds(175,150,50,30);
        f.add(lMinits);
        //----- JTextField Minits
        final JTextField tMinits = new JTextField();
        tMinits.setBounds(175,180,30,30);
        f.add(tMinits);
        //----- JLabel Seconds
        final JLabel lSec = new JLabel("Seconds");
        lSec.setBounds(215,150,50,30);
        f.add(lSec);
        //----- JTextField Seconds
        final JTextField tSec = new JTextField();
        tSec.setBounds(215,180,30,30);
        f.add(tSec);
        //----- JLabel day
        final JLabel lday = new JLabel("Day:");
        lday.setBounds(10,150,30,30);
        f.add(lday);
        //----- JTextField day
        final JTextField tday = new JTextField();
        tday.setBounds(65,150,60,30);
        f.add(tday);
        //----- JLabel Manth
        final JLabel lManth = new JLabel("Month:");
        lManth.setBounds(10,180,50,30);
        f.add(lManth);
        //----- JTextField Manth
        final JTextField tManth = new JTextField();
        tManth.setBounds(65,180,60,30);
        f.add(tManth);
        //----- JLabel Year
        final JLabel lYear = new JLabel("Year:");
        lYear.setBounds(10,210,50,30);
        f.add(lYear);
        //----- JTextField Year
        final JTextField tYear = new JTextField();
        tYear.setBounds(65,210,60,30);
        f.add(tYear);
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
                Calendar cal = Calendar.getInstance();
                id1 = (Integer) taskList[i[0]][4];
                TaskInfo t1 =  tasks.get(id1);
                cal.setTime(t1.getDate());
                tYear.setText(cal.get(Calendar.YEAR)+"");
                tManth.setText((cal.get(Calendar.MONTH)+1)+"");
                tday.setText(cal.get(Calendar.DAY_OF_MONTH)+"");
                thour.setText(cal.get(Calendar.HOUR)+"");
                tMinits.setText(cal.get(Calendar.MINUTE)+"");
                tSec.setText(cal.get(Calendar.SECOND)+"");
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
                Calendar cal = Calendar.getInstance();
                id1 = (Integer) taskList[i[0]][4];
                TaskInfo t1 =  tasks.get(id1);
                cal.setTime(t1.getDate());
                tYear.setText(cal.get(Calendar.YEAR)+"");
                tManth.setText((cal.get(Calendar.MONTH)+1)+"");
                tday.setText(cal.get(Calendar.DAY_OF_MONTH)+"");
                thour.setText(cal.get(Calendar.HOUR)+"");
                tMinits.setText(cal.get(Calendar.MINUTE)+"");
                tSec.setText(cal.get(Calendar.SECOND)+"");
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
                int Year = 0;
                int Manth = 0;
                int Day = 0;
                int Hours = 0;
                int Minits = 0;
                int Seconds = 0;
                    boolean ret = false;
                    boolean year = true;
                    boolean month = true;
                    boolean day = true;
                    boolean hour = true;
                    boolean min = true;
                    boolean sec = true;
                // Validation
                try {
                    Year = Integer.parseInt(tYear.getText());
                } catch (NumberFormatException e) {
                    tYear.setForeground(new Color(255,10,10));
                    tYear.setBackground(new Color(0,50,0));
                    ret = true;
                    year = false;
                }
                try {
                    Manth = Integer.parseInt(tManth.getText());
                } catch (NumberFormatException e) {
                    tManth.setForeground(new Color(255,10,10));
                    tManth.setBackground(new Color(0,50,0));
                    ret = true;
                    month = false;
                }
                try {
                    Day = Integer.parseInt(tday.getText());
                } catch (NumberFormatException e) {
                    tday.setForeground(new Color(255,10,10));
                    tday.setBackground(new Color(0,50,0));
                    ret = true;
                    day = false;
                }
                try {
                    Hours = Integer.parseInt(thour.getText());
                } catch (NumberFormatException e) {
                    thour.setForeground(new Color(255,10,10));
                    thour.setBackground(new Color(0,50,0));
                    ret = true;
                    hour = false;
                }
                try {
                    Minits = Integer.parseInt(tMinits.getText());
                } catch (NumberFormatException e) {
                    tMinits.setForeground(new Color(255,10,10));
                    tMinits.setBackground(new Color(0,50,0));
                    ret = true;
                    min = false;
                }
                try {
                    Seconds = Integer.parseInt(tSec.getText());
                } catch (NumberFormatException e) {
                    tSec.setForeground(new Color(255,10,10));
                    tSec.setBackground(new Color(0,50,0));
                    ret = true;
                    sec = false;
                }
                
                if ((Year > 2111) || (Year < 2011)) {
                    tYear.setForeground(new Color(255,10,10));
                    tYear.setBackground(new Color(0,50,0));
                    ret = true;
                } else if (year != false){
                    tYear.setForeground(Color.BLACK);
                    tYear.setBackground(Color.WHITE);
                }
                if ((Hours > 24) || (Hours < 0)) {
                    thour.setForeground(new Color(255,10,10));
                    thour.setBackground(new Color(0,50,0));
                    ret = true;
                } else if (hour != false){
                    thour.setForeground(new Color(0,0,0));
                    thour.setBackground(new Color(255,255,255));
                }
                if ((Minits > 59) || (Minits < 0)) {
                    tMinits.setForeground(new Color(255,10,10));
                    tMinits.setBackground(new Color(0,50,0));
                    ret = true;
                } else if (min != false){
                    tMinits.setForeground(new Color(0,0,0));
                    tMinits.setBackground(new Color(255,255,255));
                }
                if ((Seconds > 60) || (Seconds < 0)) {
                    tSec.setForeground(new Color(255,10,10));
                    tSec.setBackground(new Color(0,50,0));
                    ret = true;
                } else if (sec != false){
                    tSec.setForeground(new Color(0,0,0));
                    tSec.setBackground(new Color(255,255,255));
                }
                if ((Manth > 12) || (Manth < 1)) {
                    tManth.setForeground(new Color(255,10,10));
                    tManth.setBackground(new Color(0,50,0));
                    ret = true;
                } else if (month != false){
                    tManth.setForeground(new Color(0,0,0));
                    tManth.setBackground(new Color(255,255,255));
                }
                if ((Day > 31) || (Manth < 1)) {
                    tday.setForeground(new Color(255,10,10));
                    tday.setBackground(new Color(0,50,0));
                    ret = true;
                } else if (day != false){
                    tday.setForeground(new Color(0,0,0));
                    tday.setBackground(new Color(255,255,255));
                }
                 switch (Manth) {
                    case 1:{}
                    case 3:{}
                    case 5:{}
                    case 7:{}
                    case 8:{}
                    case 10:{}
                    case 11:{}
                    case 12: {
                        if ((Day > 31) || (Day < 1)) {
                            tday.setForeground(new Color(255,10,10));
                            tday.setBackground(new Color(0,50,0));
                            ret = true;
                        } else {
                            tday.setForeground(new Color(0,0,0));
                            tday.setBackground(new Color(255,255,255));
                        }
                        break;
                    }
                    case 4: {}
                    case 6: {}
                    case 9: {
                        if ((Day > 30) || (Day < 1)) {
                            tday.setForeground(new Color(255,10,10));
                            tday.setBackground(new Color(0,50,0));
                            ret = true;
                        } else {
                            tday.setForeground(new Color(0,0,0));
                        tday.setBackground(new Color(255,255,255));
                        }
                        break;
                    }
                    case 2 : {
                        if (Year % 4 == 0) {
                            if ((Day > 29) || (Day < 1)) {
                                tday.setForeground(new Color(255,10,10));
                                tday.setBackground(new Color(0,50,0));
                                ret = true;
                            } else {
                                tday.setForeground(new Color(0,0,0));
                                tday.setBackground(new Color(255,255,255));
                            }
                        } else {
                            if ((Day > 28) || (Day < 1)) {
                                tday.setForeground(new Color(255,10,10));
                                tday.setBackground(new Color(0,50,0));
                                ret = true;
                            } else {
                                tday.setForeground(new Color(0,0,0));
                                tday.setBackground(new Color(255,255,255));
                            }
                        }
                        break;
                    }
                 }
                if (ret) {
                    return;
                }
                // End Validation
                Calendar cal = Calendar.getInstance();
                cal.set(Year,Manth-1,Day,Hours,Minits,Seconds);    
                TaskInfo ts = new TaskInfoImpl();
                ts.setName(tname.getText());
                ts.setInfo(tinfo.getText());
                ts.setDate(cal.getTime());
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
            tYear.setEnabled(false);
            tManth.setEnabled(false);
            tday.setEnabled(false);
            thour.setEnabled(false);
            tMinits.setEnabled(false);
            tSec.setEnabled(false);
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
        if (ts.getExec() != null){
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
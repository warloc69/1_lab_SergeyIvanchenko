package lab.view;
import lab.conntroller.ManagerControllerInterface;
import lab.*;
import lab.model.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.text.*;
/**
 * Create user Interface
 */
public class ManagerView extends JFrame implements Observer, Runnable {
    private boolean autoRun = false;
    private int H = 500;
    private int W = 600;
    private int offTime = 0;
    private Integer id1 =-1;
    private ManagerControllerInterface controller;
    private ModelGetInf mgi = null;
    private Container pane = null;
    private int loadFirstView = 0;
    private TableModel tableModel;
    private int lastSelected = -1;
    private boolean start = false;    
    private JTable table = null;
    private Hashtable<Integer,TaskInfo> tasks = null;
    private Thread thread = null;
    private JMenuBar menuBar = null;
    private JMenu menuCom = null;
    private JMenu menuAbout = null;
    private JMenu menuOpt = null;
    public static final long serialVersionUID = 123312332l;
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
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuCom = new JMenu("Command");
        menuBar.add(menuCom);
        menuOpt = new JMenu("Option");
        menuBar.add(menuOpt);
        menuAbout = new JMenu("About");
        menuBar.add(menuAbout);
        pane = getContentPane();
        setTitle("Task Manager");
        loadOption();
        setSize(W,H);
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    saveOption();
                    dispose();
                    System.exit(0);
                }
            }
        );
    }
    /**
    *    Load all swing element.
    */
    private void loadView(){
        if (loadFirstView != 1) {
            loadFirstView = 1;
            //------- Button Add Task
            ImageIcon add = new ImageIcon("img\\add.png");
           final JMenuItem bAddtask = new JMenuItem("Add task",add);
           menuCom.add(bAddtask);
            bAddtask.setToolTipText("Add new task");
            bAddtask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        viewAddTask(0);
                    }
                }
            );
           // paneB.add(bAddtask);
            //------- Button Remove Task
            ImageIcon remove = new ImageIcon("img\\remove.png");
            JMenuItem bRemovetask = new JMenuItem("Remove task",remove);
            menuCom.add(bRemovetask);
            bRemovetask.setToolTipText("Remove selected task");
            bRemovetask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        int[] i = table.getSelectedRows();
                        if (i.length == 0) {return;}
                        int id = tableModel.getID(i[0]);
                        lastSelected = i[0];
                        controller.delTask(id);
                    }
                }
            );
            //paneB.add(bRemovetask);
            //------ Button Edit Task
            ImageIcon edit = new ImageIcon("img\\edit.png");
            final JMenuItem bEdittask = new JMenuItem("Edit task",edit);
            menuCom.add(bEdittask);
            bEdittask.setToolTipText("Edit selected task");
            bEdittask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        viewEditTask();
                    }
                }
            );
            //paneB.add(bEdittask);
            //------ Button View Task
            ImageIcon view = new ImageIcon("img\\view.png");
            final JMenuItem bViewtask = new JMenuItem("View task",view);
            menuCom.add(bViewtask);
            bViewtask.setToolTipText("View selected task");
            bViewtask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        viewViewTask();
                    }
                }
            );
           // paneB.add(bViewtask);
            addComponentListener(
                new ComponentAdapter() {
                    public void componentResized(ComponentEvent e) {
                        repaint();
                    }
                }
            );
            final JMenuItem bAbout = new JMenuItem("About program");
            menuAbout.add(bAbout);
            bAbout.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    aboutProgram();
                }
            }
            );
            final JMenuItem option = new JMenuItem("Option");
            menuOpt.add(option);
            option.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    optionProgram();
                }
            }
            );
            
            setVisible(true);
        }
    }

    /**
    *    Show add / edit / view task dialog.
    */
    private void viewAddTask(final int command){
        int W = 1024;
        int H = 600;  
        final JDialog f = new JDialog(this, true);
        Box boxName = Box.createHorizontalBox();
        Box boxInfo = Box.createHorizontalBox();
        Box boxFile = Box.createHorizontalBox();
        Box boxButton = Box.createHorizontalBox();
        Box allBoxes = Box.createVerticalBox();
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
        boxName.add(lname);
        //----- JTextField
        final JTextField tname = new JTextField();
        tname.setToolTipText("Writes task name here");
        
        tname.setMaximumSize(new Dimension(f.getSize().height,30));
        
        boxName.add(tname);
        
        //----- JLabel Info
        final JLabel linfo = new JLabel("Info:");
        //----- JTextArea Info        
        final JTextArea tinfo = new JTextArea();
        JScrollPane sinfo = new JScrollPane(tinfo);
        tinfo.setToolTipText("Writes information about task here");
        boxInfo.add(sinfo);
        //----- Date
        final JSpinner dateChooser = new JSpinner(new SpinnerDateModel());
        dateChooser.setToolTipText("Writes moment when task must run");
        dateChooser.setMaximumSize(new Dimension(110,30));
        boxName.add(Box.createRigidArea(new Dimension(10,30)));    
        boxName.add(dateChooser);
        //----- JLabel file
        final JLabel lFile = new JLabel("Program:");
        //----- JChoose Execute file
        final JFileChooser exefile = new JFileChooser();   
        exefile.setFileFilter(new ExeFilter());
        JButton bexefile = new JButton("Run program");
        bexefile.setToolTipText("If you want run some program, you can choose it's program here ");  
        bexefile.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                exefile.showOpenDialog(f);
                lFile.setText(exefile.getSelectedFile().getPath());
            }
        });
        boxFile.add(Box.createRigidArea(new Dimension(10,30)));
        boxFile.add(bexefile);
        boxFile.add(lFile);
        //----- Button Save
        JButton save = null;
        switch (command) {
            case 0: { 
                save = new JButton("Save");
                save.setToolTipText("Save the task");
                f.setTitle("Add task");
                break;
            }
            case 1: {
                save = new JButton("Save");
                save.setToolTipText("Save the task");
                f.setTitle("Edit task");
                int[] i = table.getSelectedRows();
                if (i.length == 0) {return;}
                id1 = tableModel.getID(i[0]);
                lastSelected = i[0];
                TaskInfo t1 =  tasks.get(id1);
                dateChooser.setValue(t1.getDate());
                tname.setText(t1.getName());
                tinfo.setText(t1.getInfo());
                if (t1.getExec() != null) {
                    lFile.setText("RunProgram: " + t1.getExec().getPath());
                    exefile.setSelectedFile(t1.getExec());
                }
                break;
            }
            case 2: {
                save = new JButton("Ok");
                save.setToolTipText("Close dialog");
                f.setTitle("View task");
                int[] i = table.getSelectedRows();
                if (i.length == 0) {return;}
                id1 = tableModel.getID(i[0]);
                lastSelected = i[0];
                TaskInfo t1 =  tasks.get(id1);
                dateChooser.setValue(t1.getDate());
                tname.setText(t1.getName());
                tinfo.setText(t1.getInfo());
                if (t1.getExec() != null) {
                    lFile.setText("RunProgram: " + t1.getExec().getPath());
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
                ts.setDate((Date)dateChooser.getValue());
                if (ts.getDate().before(new Date())) {
                    dateChooser.setBackground(new Color(100,0,0));
                    repaint();
                    return;
                }
                ts.setName(tname.getText());
                ts.setInfo(tinfo.getText());
                if (exefile.getSelectedFile() != null) {
                    ts.setExec(exefile.getSelectedFile());
                } else {
                    ts.setExec(new File(" "));
                }
                if (command == 1) {
                    if(!controller.editTask(id1,ts)) {
                        return;
                    }
                } else {
                    if(!controller.addTask(ts)) {
                        return;
                    }
                }
                f.dispose();
            }
        });
        boxButton.add(save);
        //----- Button Cancel
        JButton cancel = null;
        if (command != 2){
            cancel = new JButton("Cancel");
            save.setToolTipText("Close the dialog");
            cancel.setBounds(115,H/2-70,100,30);    
            cancel.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    f.dispose();
                }            
            });
            boxButton.add(cancel);   
        }
        
        f.setResizable(false);
        if (command == 2) {
            tname.setEnabled(false);
            tinfo.setEnabled(false);
            dateChooser.setEnabled(false);
            boxFile.remove(bexefile);
        }
        allBoxes.add(boxName);
        allBoxes.add(Box.createRigidArea(new Dimension(10,10)));
        allBoxes.add(linfo, BorderLayout.WEST);        
        allBoxes.add(boxInfo);
        allBoxes.add(boxFile,BorderLayout.WEST);
        allBoxes.add(boxButton);
        f.add(allBoxes);
        f.setVisible(true);
    }

    /**
     *    Show task Edit dialog.
     */
    private void viewEditTask(){
        viewAddTask(1);
    }

    /**
     *    Show task Edit dialog.
     */
    private void viewViewTask(){
        viewAddTask(2);
    }
    /**
    *    Show execute dialog.
    */
    private void viewMassage(final TaskInfo ts) {
    start = false;
    int W = Toolkit.getDefaultToolkit().getScreenSize().width;
    int H = Toolkit.getDefaultToolkit().getScreenSize().height-50;
    final JSpinner dateChooser = new JSpinner(new SpinnerDateModel());
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
        JButton ok = new JButton("Ok");
        ok.setBounds(15,H/2-70,100,30);    
        ok.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (dateChooser.isEnabled()) {
                    ts.setDate((Date)dateChooser.getValue());
                    lastSelected = tableModel.getSelectedRow(ts);                    
                    if (!controller.editTask(ts.getID(),ts)) {    
                        return;
                    }
                    dateChooser.setEnabled(false);
                    msg.dispose();
                    return;
                } 
                if(!dateChooser.isEnabled()) {
                    if (!start) {
                        if (ts.getExec() != null && !ts.getExec().getName().equals(" ")){
                            Runtime r = Runtime.getRuntime();
                            try {
                                r.exec(ts.getExec().getPath());
                                start = true;
                            } catch (IOException e) {}
                        }
                    }
                    lastSelected = tableModel.getSelectedRow(ts);    
                    controller.delTask(ts.getID());
                }
                msg.dispose();
                
            }
        });
        if (start) {
            return;
        }
        msg.add(ok);
        //-----Date chooser
        dateChooser.setBounds(65,160,160,30);
        dateChooser.setEnabled(false);
        msg.add(dateChooser);
        //-----Put off
        JButton cancel = new JButton("Put off");
        cancel.setToolTipText("Put off task to the future time");
            cancel.setBounds(115,H/2-70,100,30);    
            cancel.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    dateChooser.setEnabled(true);
                }            
            });
        msg.add(cancel);
        if (autoRun) {
            if (ts.getExec() != null && !ts.getExec().getName().equals(" ")){
                Runtime r = Runtime.getRuntime();
                try {
                    r.exec(ts.getExec().getPath());
                    start = true;
                } catch (IOException e) {}
            }
        }
        msg.setVisible(true);
    }
    /**
    * Shows about dialog.
    */
    private void aboutProgram() {
        JDialog  aboutD = new JDialog(this, true);
        aboutD.setTitle("About program");
        Box b = Box.createVerticalBox();
        aboutD.add(b);
        Border e = BorderFactory.createMatteBorder(10,10,10,10,Color.BLUE);
        aboutD.setSize(400,400);
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n    Эта программа распостраняется как есть."+"\n");
        sb.append("    Автор не несет ответственности за возможные"+"\n");
        sb.append("    сбои в работе компютера связаные с работой"+"\n");
        sb.append("    данной программы, за случайное "+"\n");
        sb.append("    форматирование жосткого диска и утерю ");
        sb.append("информации."+"\n");
        sb.append("\n\n\n    Подвал-Холдинг, мы глючим для Вас :)");
        JTextArea aboutL = new JTextArea(sb.toString());
        b.add(aboutL);
        aboutL.setBorder(e);
        aboutL.setEnabled(false);
        aboutL.setBackground(new Color(100,0,0));
        aboutD.setVisible(true);
    }
    /**
    * Shows option dialog.
    */
    private void optionProgram() {
        final JDialog optionD = new JDialog(this,true);
        optionD.setTitle("Option");
        optionD.setSize(250, 105);
        Box b = Box.createVerticalBox();
        Box b1 = Box.createHorizontalBox();
        Box b2 = Box.createHorizontalBox();
        optionD.add(b);
        final JCheckBox check = new JCheckBox("Autorun program");
        check.setSelected(autoRun);
        check.setToolTipText("Set, if you want to run executable program automaticly in the task.");
        JLabel l = new JLabel("Put off time:");
        JLabel m = new JLabel("Minits");
        final JTextField min = new JTextField();
        min.setText(offTime+"");
        JButton bOk = new JButton("Ok");
        bOk.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {                
                        autoRun = check.isSelected();
                        H = getSize().height;
                        W = getSize().width;
                    try {
                        offTime = Integer.parseInt(min.getText());
                    } catch (ClassCastException e2) {
                        return;
                    } catch (NumberFormatException e3) {
                        return;
                    }
                    optionD.dispose();
                }
            }
        );
        JButton bCancel = new JButton("Cancel");
        bCancel.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    optionD.dispose();
                }
            }
        );
        b1.add(l);
        b1.add(Box.createGlue());
        b1.add(min);
        b1.add(m);
        b.add(b1);
        b.add(check);
        b.add(b2);        
        b2.add(bOk);
        b2.add(Box.createGlue());
        b2.add(bCancel);
        optionD.setResizable(false);        
        optionD.setVisible(true);
    }
    /**
    * Loads option from the file.
    */
    private void loadOption() {
        try {
            File f = new File("option\\option.opt");
            if(!f.exists()) {
                return;
            }
            DataInputStream in = 
                new DataInputStream(
                    new FileInputStream(f));
            offTime = in.readInt();
            autoRun = in.readBoolean();
            H = in.readInt();
            W = in.readInt();
        } catch (IOException e) {}
    }
    /**
    * Saves option into the file.
    */
    private void saveOption() {
        File f = new File("option\\option.opt");
        DataOutputStream out = null;
        try {
            out = 
                new DataOutputStream(
                    new FileOutputStream(f));
            out.writeInt(offTime);
            out.flush();
            out.writeBoolean(autoRun);
            out.flush();
            out.writeInt(getSize().height);
            out.flush();
            out.writeInt(getSize().width);
            out.flush();
            out.close();
        } catch (IOException e1){
        }
    }
    /**
    *    Create new thread look in all task, 
    *     and chooses the task for execution. 
    */
    public void run() {
        while (true) {    
            try {
                thread.sleep(1000);
            } catch (InterruptedException e) {}
                if (tasks != null) {
                    Collection<TaskInfo> col = tasks.values();
                    try {
                        for (TaskInfo t :col) {                    
                            if (t.getDate().getTime() <= (new Date().getTime()+offTime*1000*60)) {
                                viewMassage(t);
                            }
                        }
                    } catch (ConcurrentModificationException e ) {
                        col = tasks.values();
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
     * Update informatio about tasks 
     * @param obj keep information about tasks
     */
    public void update(Observable obs, Object obj){
        int com = 0;
        int id = 0;
        if (obj.getClass() != Long.class) {
            mgi = (ModelGetInf) obj;
        } else {
            Long data = (Long) obj;
            Long com1 = (data >> 31);
            com = com1.intValue();
            Long comTemp = (com1 << 31);
            Long idTemp = (data - comTemp);
            Long l = new Long(idTemp);
            id = l.intValue();
        }
        switch (com) {
            case 0: {
                tasks = mgi.getAllTasks();
                tableModel = new TableModel(tasks);
                break;
            }
            case 3: {
                tasks.put(id,mgi.getTask(id));
                tableModel.editTask(lastSelected,tasks.get(id));
                break;
            }
            case 1: {
                tasks.remove(id);
                tableModel.removeTask(lastSelected);
                break;
            }
            case 2: {
                tasks.put(id,mgi.getTask(id));
                tableModel.addTask(tasks.get(id));
                break;
            }
        }
        //------- Create JTable            
        if (table == null) {
            table = new JTable(tableModel);
            table.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            viewEditTask();
                        }
                    }
                }
            );
            JScrollPane scr = new JScrollPane(table);
            pane.add(scr);   
        } else {
            tableModel.fireTableDataChanged();
        }
        loadView();
    }
}//end ManagerView
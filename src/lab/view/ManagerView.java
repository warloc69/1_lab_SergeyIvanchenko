package lab.view;
import lab.conntroller.ManagerControllerInterface;
import lab.*;
import lab.model.*;
import lab.exception.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.text.*;
import javax.swing.JOptionPane.*;
/**
 * Class Create user Interface
 */
public class ManagerView extends JFrame implements lab.model.observer.Observable, Runnable {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ManagerView.class);
    private Long id1 =-1l;
    private ManagerControllerInterface controller;
    private ModelGetInf mgi = null;
    private Container pane = null;
    private int loadFirstView = 0;
    private TableModel tableModel;
    private int lastSelected = -1;
    private boolean start = false;    
    private JTable table = null;
    private Hashtable<Long,TaskInfo> tasks = null;
    private Thread thread = null;
    private JMenuBar menuBar = null;
    private JMenu menuCom = null;
    private JMenu menuAbout = null;
    private JMenu menuOpt = null;
    private JLabel total = null;
    private JLabel today = null;
    private JLabel tomorrow = null;
    private JLabel week = null;    
    public static final long serialVersionUID = 123312332l;
    /**
    *    It's private class helps to show only programs
    */
    private class ExeFilter extends javax.swing.filechooser.FileFilter {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".exe") ||
                    f.isDirectory();
            }
            public String getDescription() {
                return "Program";
            }
        }
    /**
    *    Constructor creates ManagerView's object.
    */
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
        setSize(ViewVariable.W,ViewVariable.H);
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
            //------- Menu item New Task
            ImageIcon add = new ImageIcon("img\\add.png");
           final JMenuItem bAddtask = new JMenuItem("New task",add);
           menuCom.add(bAddtask);
           bAddtask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));
           bAddtask.setToolTipText("Add new task");
            bAddtask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        viewAddTask(0);
                    }
                }
            );
            //------- Menu item Remove Task
            ImageIcon remove = new ImageIcon("img\\remove.png");
            JMenuItem bRemovetask = new JMenuItem("Remove task",remove);
            menuCom.add(bRemovetask);
            bRemovetask.setToolTipText("Remove selected task");
            bRemovetask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,InputEvent.CTRL_MASK));
            bRemovetask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        int[] i = table.getSelectedRows();
                        if (i.length == 0) {return;}
                        long id = tableModel.getID(i[0]);
                        lastSelected = i[0];
                        try {
                            controller.delTask(id);
                        } catch (DataAccessException e) {
                            JOptionPane.showMessageDialog(ManagerView.this, e,"Error",JOptionPane.ERROR_MESSAGE);                    
                        }
                    }
                }
            );
            //------ Menu item Edit Task
            ImageIcon edit = new ImageIcon("img\\edit.png");
            final JMenuItem bEdittask = new JMenuItem("Edit task",edit);
            menuCom.add(bEdittask);
            bEdittask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,InputEvent.CTRL_MASK));
            bEdittask.setToolTipText("Edit selected task");
            bEdittask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        viewEditTask();
                    }
                }
            );
            //------ Menu Item View Task
            ImageIcon view = new ImageIcon("img\\view.png");
            final JMenuItem bViewtask = new JMenuItem("View task",view);
            menuCom.add(bViewtask);
            bViewtask.setToolTipText("View selected task");
            bViewtask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));
            bViewtask.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        viewViewTask();
                    }
                }
            );
            // action resized
            addComponentListener(
                new ComponentAdapter() {
                    public void componentResized(ComponentEvent e) {
                        repaint();
                    }
                }
            );
            //------ Menu Item About program
            final JMenuItem bAbout = new JMenuItem("About program");
            menuAbout.add(bAbout);
            bAbout.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    aboutProgram();
                }
            }
            );
            //------ Menu Item Option
            final JMenuItem option = new JMenuItem("Option");
            menuOpt.add(option);
            option.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
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
        final Box b = Box.createHorizontalBox();
        b.add(dateChooser);
        boxName.add(b);
        final JComboBox list = new JComboBox();
        list.addItem("0.5");
        list.addItem("1");
        list.addItem("2");
        list.addItem("5");
        list.addItem("6");
        list.addItem("10");
        list.addItem("12");
        list.addItem("15");
        list.addItem("24");
        list.setMaximumSize(new Dimension(45,30));
        final JLabel h = new JLabel("H");
        list.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    Double d = Double.parseDouble((String) list.getSelectedItem())*60;
                    Long l = d.longValue();
                     int[] i = table.getSelectedRows();
                    if (i.length == 0) {return;}
                    id1 = tableModel.getID(i[0]);
                    lastSelected = i[0];
                    TaskInfo t1 =  tasks.get(id1);
                    dateChooser.setValue(new Date(t1.getDate().getTime()+l*1000*60));
                }
            }
        );
        boxName.add(list);
        boxName.add(h);
        //----- JLabel file
        final JLabel lFile = new JLabel("");
        //----- JChoose Execute file
        final JFileChooser exefile = new JFileChooser();   
        exefile.setFileFilter(new ExeFilter());
        JButton bexefile = new JButton("Run program");
        bexefile.setToolTipText("If you want run some program, you can choose it's program here ");  
        bexefile.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                exefile.showOpenDialog(f);
                if (exefile.getSelectedFile() != null ) {
                    lFile.setText(exefile.getSelectedFile().getPath());
                    lFile.setForeground(Color.BLACK);
                }
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
                f.setTitle("Add new task");
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
                TaskInfo t1 =  tableModel.get(i[0]);
                dateChooser.setValue(t1.getDate());
                tname.setText(t1.getName());
                tinfo.setText(t1.getInfo());
                if (t1.getExec() != null && !t1.getExec().getName().equals(" ")) {
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
                TaskInfo t1 =  tableModel.get(i[0]);
                dateChooser.setValue(t1.getDate());
                tname.setText(t1.getName());
                tinfo.setText(t1.getInfo());
                if (t1.getExec() != null && !t1.getExec().getName().equals(" ")) {
                    lFile.setText("RunProgram: " + t1.getExec().getPath());
                    exefile.setSelectedFile(t1.getExec());
                }
                break;
            }
        }      
        save.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (command == 2) {
                    f.dispose();
                    return;
                }
                TaskInfo ts = new TaskInfoImpl();
                ts.setDate((Date)dateChooser.getValue());
                ts.setName(tname.getText());
                ts.setInfo(tinfo.getText());
                if (exefile.getSelectedFile() != null) {
                    ts.setExec(exefile.getSelectedFile());
                } else {
                    ts.setExec(new File(" "));
                }
                if (command == 1) {
                    try {
                        controller.editTask(id1,ts);
                    } catch (DataAccessException e) {
                        JOptionPane.showMessageDialog(ManagerView.this, e,"Error",JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    } catch (BadTaskException e) {
                        JOptionPane.showMessageDialog(ManagerView.this, e,"Warning",JOptionPane.WARNING_MESSAGE);
                        return;
                    } 
                } else {
                   try {
                       controller.addTask(ts);
                    } catch (DataAccessException e) {
                        JOptionPane.showMessageDialog(ManagerView.this, e,"Error",JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    } catch (BadTaskException e) {
                        JOptionPane.showMessageDialog(ManagerView.this, e,"Warning",JOptionPane.WARNING_MESSAGE);
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
            boxName.remove(list);
            boxName.remove(h);
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
     *    Show task View dialog.
     */
    private void viewViewTask(){
        viewAddTask(2);
    }
    /**
    *    Show execute dialog.
    */
    private void viewMassage(final TaskInfo ts) {
        final Box boxName = Box.createHorizontalBox();
        Box boxInfo = Box.createHorizontalBox();
        Box boxFile = Box.createHorizontalBox();
        Box boxButton = Box.createHorizontalBox();
        Box allBoxes = Box.createVerticalBox();
        start = false;
        int W = Toolkit.getDefaultToolkit().getScreenSize().width;
        int H = Toolkit.getDefaultToolkit().getScreenSize().height-50;
        final JSpinner dateChooser = new JSpinner(new SpinnerDateModel());
        dateChooser.setValue(ts.getDate());
        dateChooser.setMaximumSize(new Dimension(110,30));
        final JComboBox list = new JComboBox();
        list.addItem("0.5");
        list.addItem("1");
        list.addItem("2");
        list.addItem("5");
        list.addItem("6");
        list.addItem("10");
        list.addItem("12");
        list.addItem("15");
        list.addItem("24");
        list.setMaximumSize(new Dimension(45,30));
        final JLabel h = new JLabel("H");
        list.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    Double d = Double.parseDouble((String) list.getSelectedItem())*60;
                    Long l = d.longValue();
                    lastSelected = tableModel.getSelectedRow(ts);
                    dateChooser.setValue(new Date(new Date().getTime()+l*1000*60));
                }
            }
        );
        final JDialog msg = new JDialog(this, true);
        msg.setSize(W/2,H/2);
        msg.setBounds(W/4,H/4,W/2,H/2);
                msg.addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    lastSelected = tableModel.getSelectedRow(ts);
                    try {
                        controller.delTask(ts.getID());
                    } catch (DataAccessException e) {
                        JOptionPane.showMessageDialog(msg, e,"Error",JOptionPane.ERROR_MESSAGE);
                    }
                    msg.dispose();
                    loadView();
                }
            }
        );
        //----- JLabel name
        final JLabel lname = new JLabel("Name:");
        boxName.add(lname);
        //----- JTextField
        final JTextField tname = new JTextField();
        tname.setMaximumSize(new Dimension(msg.getSize().height,30));        
        boxName.add(tname);
        tname.setText(ts.getName());
        tname.setEnabled(false);
        //----- JLabel Info
        final JLabel linfo = new JLabel("Info:");
        //----- JTextArea Info
        final JTextArea tinfo = new JTextArea();
        JScrollPane scrol = new JScrollPane(tinfo);
        boxInfo.add(scrol);
        tinfo.setText(ts.getInfo());
        tinfo.setEnabled(false);
        boxName.add(Box.createRigidArea(new Dimension(10,30)));    
        boxName.add(dateChooser);
        if (ts.getExec() != null && !ts.getExec().getName().equals(" ")) {
            final JLabel lFile = new JLabel("Run program : " + ts.getExec().getName());
            boxFile.add(lFile);
        }
        JButton ok = new JButton("Ok");  
        ok.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (dateChooser.isEnabled()) {
                    ts.setDate((Date)dateChooser.getValue());
                    lastSelected = tableModel.getSelectedRow(ts);                    
                    try {
                        controller.editTask(ts.getID(),ts);
                    } catch (DataAccessException e) {
                        JOptionPane.showMessageDialog(msg, e,"Error",JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    } catch (BadTaskException e) {
                        JOptionPane.showMessageDialog(msg, e,"Warning",JOptionPane.WARNING_MESSAGE);
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
                            } catch (IOException e) {
                                log.error("Runtime error");
                            }
                        }
                    }
                    lastSelected = tableModel.getSelectedRow(ts);    
                    try {
                        controller.delTask(ts.getID());
                    } catch (DataAccessException e) {
                        JOptionPane.showMessageDialog(msg, e,"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
                msg.dispose();
                
            }
        });
        if (start) {
            return;
        }
        boxButton.add(ok);
        //-----Date chooser
        dateChooser.setEnabled(false);
        //-----Put off
        JButton cancel = new JButton("Postpone");
        cancel.setToolTipText("Put off task to the future time");
            cancel.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    dateChooser.setEnabled(true);
                    boxName.add(list);
                    boxName.add(h);
                    msg.setVisible(true);
                }            
            });
        boxButton.add(cancel);
        if (ViewVariable.autoRun) {
            if (ts.getExec() != null && !ts.getExec().getName().equals(" ")){
                Runtime r = Runtime.getRuntime();
                try {
                    r.exec(ts.getExec().getPath());
                    start = true;
                } catch (IOException e) {
                    log.error("Runtime error");
                }
            }
        }
        allBoxes.add(boxName);
        allBoxes.add(Box.createRigidArea(new Dimension(10,10)));
        allBoxes.add(linfo, BorderLayout.WEST);        
        allBoxes.add(boxInfo);
        allBoxes.add(boxFile,BorderLayout.WEST);
        allBoxes.add(boxButton);
        msg.add(allBoxes);
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
        optionD.setSize(255, 105);
        Box b = Box.createVerticalBox();
        Box b1 = Box.createHorizontalBox();
        Box b2 = Box.createHorizontalBox();
        optionD.add(b);
        final JCheckBox check = new JCheckBox("Autorun program");
        check.setSelected(ViewVariable.autoRun);
        check.setToolTipText("Set, if you want to run executable program automaticly in the task.");        
        JLabel l = new JLabel("Put off time:");
        JLabel m = new JLabel("Minits");
        final JTextField min = new JTextField();
        min.setText(ViewVariable.offTime+"");
        final JComboBox list = new JComboBox();
        list.addItem("0.5");
        list.addItem("1");
        list.addItem("2");
        list.addItem("5");
        list.addItem("6");
        list.addItem("10");
        list.addItem("12");
        list.addItem("15");
        list.addItem("24");
        JLabel h = new JLabel("H");
        list.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    Double d = Double.parseDouble((String) list.getSelectedItem());
                    Integer i = d.intValue()*60;
                    min.setText(i.toString());
                }
            }
        );
        JButton bOk = new JButton("Ok");
        bOk.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {                
                        ViewVariable.autoRun = check.isSelected();
                        ViewVariable.H = getSize().height;
                        ViewVariable.W = getSize().width;
                    try {
                        ViewVariable.offTime = Integer.parseInt(min.getText());
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
        b1.add(list);
        b1.add(h);
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
            ViewVariable.offTime = in.readInt();
            ViewVariable.autoRun = in.readBoolean();
            ViewVariable.H = in.readInt();
            ViewVariable.W = in.readInt();
        } catch (IOException e) {
            log.error("IO Exception, read option");
        }
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
            out.writeInt(ViewVariable.offTime);
            out.flush();
            out.writeBoolean(ViewVariable.autoRun);
            out.flush();
            out.writeInt(getSize().height);
            out.flush();
            out.writeInt(getSize().width);
            out.flush();
            out.close();
        } catch (IOException e1){
            log.error("IO Exception, write option");
        }
    }
    /**
    *    Create new thread look in all tasks, 
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
                            if (t.getDate().getTime() <= (new Date().getTime()+ViewVariable.offTime*1000*60)) {
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
     */
    public void updateTable(){
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
        table.addKeyListener( 
            new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    int com = e.getKeyCode();
                    if (e.isControlDown()) {
                        switch (com) {
                            case KeyEvent.VK_V: {
                                viewViewTask();
                                break;
                            }
                        }
                    }
                }
            }
        );
            JScrollPane scr = new JScrollPane(table);
            Box b = Box.createVerticalBox();
            b.add(scr);
            Box b1 = Box.createHorizontalBox();
            b.add(b1);
            total = new JLabel("TotalSize :" + tableModel.total); 
            total.setMaximumSize(new Dimension(100,30));
            b1.add(total);
            today = new JLabel("  Today :" + tableModel.today);
            today.setMaximumSize(new Dimension(100,30));
            b1.add(today);
            tomorrow = new JLabel("  Tomorrow :" + tableModel.tomorrow);
            tomorrow.setMaximumSize(new Dimension(100,30));
            b1.add(tomorrow);
            week = new JLabel("  This week :" + tableModel.week);
            week.setMaximumSize(new Dimension(100,30));
            b1.add(week);
            pane.add(b);
        } else {
            total.setText("TotalSize :" + tableModel.total);
            today.setText("  Today :" + tableModel.today);
            tomorrow.setText("  Tomorrow :" + tableModel.tomorrow);
            week.setText("  This week :" + tableModel.week);
            tableModel.fireTableDataChanged();
        }
        loadView();
    }
    /**
    * update all task
    */
    public void notifyGetAll(ModelGetInf inf) {
        mgi = inf;
        tasks = mgi.getAllTasks();        
        tableModel = new TableModel(tasks);
        updateTable();
    }
    /**
    * Add task into the table.
    */
    public void notifyAdd(long id) {
        try {
            tasks.put(id,mgi.getTask(id));
            tableModel.addTask(tasks.get(id));
            updateTable();
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this, e,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
    * Edit task in the table.
    */
    public void notifyEdit(long id) {
        try {
            tasks.put(id,mgi.getTask(id));
            tableModel.editTask(lastSelected,tasks.get(id));
            updateTable();
        } catch (DataAccessException e) {
            JOptionPane.showMessageDialog(this, e,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
    * Remove task from table.
    */
    public void notifyRemove(long id) {
        tasks.remove(id);        
        tableModel.removeTask(lastSelected);
        updateTable();    
    }
}//end ManagerView
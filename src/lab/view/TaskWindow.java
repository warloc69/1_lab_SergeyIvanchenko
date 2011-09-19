package lab.view;
import lab.conntroller.ManagerControllerInterface;
import lab.*;
import lab.exception.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.swing.JOptionPane.*;
/**
* Class creats add, view , edit task dialog
*/
public class TaskWindow extends JDialog {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TaskWindow.class);
    public static final long serialVersionUID = 123322332l;
    private ManagerControllerInterface controller;
    private TaskInfo task = null;
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
    * contructor used for create add dialog.
    * @param mv reference on the view.
    * @param cont reference on the controller.
    */
    public TaskWindow(ManagerView mv, ManagerControllerInterface cont) {
        super(mv,true);
        controller = cont;
        task = new TaskInfoImpl();
        viewAddTask(ViewVariable.comADD);
    }
    /**
    * contructor used for edit, view dialog.
    * @param com command for the dialog (edit or view).
    * @param mv reference on the view.
    * @param cont reference on the controller.
    * @param task reference on the views or edits task.
    */
    public TaskWindow(int com, ManagerView mv, ManagerControllerInterface cont, TaskInfo task) {
        super(mv,true);
        controller = cont;
        this.task = task;
        viewAddTask(com);        
    }
    /**
    *    Show add / edit / view task dialog.
    * @param com command for the dialog (edit, view, add).
    */
    private void viewAddTask(final int command) {
        int W = 1024;
        int H = 600;
        Box boxName = Box.createHorizontalBox();
        Box boxInfo = Box.createHorizontalBox();
        Box boxFile = Box.createHorizontalBox();
        Box boxButton = Box.createHorizontalBox();
        Box allBoxes = Box.createVerticalBox();
        setSize(W/2,H/2);
        setBounds(W/4,H/4,W/2,H/2);
        addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    dispose();
                }
            }
        );
        //----- JLabel name
        final JLabel lname = new JLabel("Name:");
        boxName.add(lname);
        //----- JTextField
        final JTextField tname = new JTextField();
        tname.setToolTipText("Writes task name here");
        
        tname.setMaximumSize(new Dimension(getSize().height,30));
        
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
                   
                    dateChooser.setValue(new Date(task.getDate().getTime()+l*1000*60));
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
                exefile.showOpenDialog(TaskWindow.this);
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
            case ViewVariable.comADD: { 
                save = new JButton("Save");
                save.setToolTipText("Save the task");
                setTitle("Add new task");
                break;
            }
            case ViewVariable.comEdit: {
                save = new JButton("Save");
                save.setToolTipText("Save the task");
                setTitle("Edit task");
                if (task.getDate().getTime() <= (new Date().getTime())) {
                    dateChooser.setValue(new Date());
                } else {
                    dateChooser.setValue(task.getDate());
                }
                tname.setText(task.getName());
                tinfo.setText(task.getInfo());
                if (task.getExec() != null && !task.getExec().getName().equals(" ")) {
                    lFile.setText("RunProgram: " + task.getExec().getPath());
                    exefile.setSelectedFile(task.getExec());
                }
                break;
            }
            case ViewVariable.comView: {
                save = new JButton("Ok");
                save.setToolTipText("Close dialog");
                setTitle("View task");
                if (task.getDate().getTime() <= (new Date().getTime())) {
                    dateChooser.setValue(new Date());
                } else {
                    dateChooser.setValue(task.getDate());
                }
                tname.setText(task.getName());
                tinfo.setText(task.getInfo());
                if (task.getExec() != null && !task.getExec().getName().equals(" ")) {
                    lFile.setText("RunProgram: " + task.getExec().getPath());
                    exefile.setSelectedFile(task.getExec());
                }
                break;
            }
        }      
        save.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (command == ViewVariable.comView) {
                    dispose();
                    return;
                }
                TaskInfo ts = new TaskInfoImpl();
                ts.setID(task.getID());
                ts.setDate((Date)dateChooser.getValue());
                ts.setName(tname.getText());
                ts.setInfo(tinfo.getText());
                if (exefile.getSelectedFile() != null) {
                    ts.setExec(exefile.getSelectedFile());
                } else {
                    ts.setExec(new File(" "));
                }
                if (command == ViewVariable.comEdit) {
                    try {
                        controller.editTask(ts.getID(),ts);
                    } catch (DataAccessException e) {
                        log.error(e);
                        JOptionPane.showMessageDialog(TaskWindow.this, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    } catch (BadTaskException e) {
                        log.warn(e);
                        JOptionPane.showMessageDialog(TaskWindow.this, e.getMessage(),"Warning",JOptionPane.WARNING_MESSAGE);
                        return;
                    } 
                } else {
                   try {
                       controller.addTask(ts);
                    } catch (DataAccessException e) {
                        log.error(e);
                        JOptionPane.showMessageDialog(TaskWindow.this, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    } catch (BadTaskException e) {
                        log.warn(e);
                        JOptionPane.showMessageDialog(TaskWindow.this, e.getMessage(),"Warning",JOptionPane.WARNING_MESSAGE);
                        return;
                    } 
                }
                dispose();
            }
        });
        boxButton.add(save);
        //----- Button Cancel
        JButton cancel = null;
        if (command != ViewVariable.comView){
            cancel = new JButton("Cancel");
            save.setToolTipText("Close the dialog");
            cancel.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    dispose();
                }            
            });
            boxButton.add(cancel);   
        }
        
        setResizable(false);
        if (command == ViewVariable.comView) {
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
        add(allBoxes);
        setVisible(true);
    }
    
}
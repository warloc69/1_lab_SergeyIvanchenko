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
* Class creats notification task dialog.
*/
public class TaskNotificationWindow extends JDialog {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TaskNotificationWindow.class);
    public static final long serialVersionUID = 123322732l;
    private ManagerControllerInterface controller;
    private TaskInfo task = null;
    private ManagerView mv = null;    
    private boolean start = false; 
    /**
    * contructor used for create notification dialog.
    * @param mv reference on the view.
    * @param cont reference on the controller.
    * @param task reference on the views task.
    */
    public TaskNotificationWindow(ManagerView mv, ManagerControllerInterface cont, TaskInfo task) {
        super(mv,true);
        this.task = task;
        controller = cont;        
        this.mv = mv;
        viewMassage(task);
    }
    /**
    *    Show execute dialog.
    * @param task reference on the views task.
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
        setSize(W/2,H/2);
        setBounds(W/4,H/4,W/2,H/2);
        addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    try {
                        controller.delTask(ts.getID());
                    } catch (DataAccessException e) {
                        log.error(e);
                        JOptionPane.showMessageDialog(TaskNotificationWindow.this, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }
                    dispose();
                }
            }
        );
        //----- JLabel name
        final JLabel lname = new JLabel("Name:");
        boxName.add(lname);
        //----- JTextField
        final JTextField tname = new JTextField();
        tname.setMaximumSize(new Dimension(getSize().height,30));        
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
        if (ts.getExec() != null && !ts.getExec().getName().equals(" ")) {
            final JLabel lFile = new JLabel("Run program : " + ts.getExec().getName());
            boxFile.add(lFile);
        }
        JButton ok = new JButton("Ok");  
        ok.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
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
                    try {
                        controller.delTask(ts.getID());
                    } catch (DataAccessException e) {
                        log.error(e);
                        JOptionPane.showMessageDialog(TaskNotificationWindow.this, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }
                
                dispose();                
            }
        });
        if (start) {
            return;
        }
        boxButton.add(ok);
        //-----Put off
        JButton cancel = new JButton("Postpone");
        cancel.setToolTipText("Put off task to the future time");
            cancel.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                   new TaskWindow(ViewVariable.comEdit,mv,controller,ts);
                   dispose();
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
        add(allBoxes);
        setVisible(true);
    }
    
}
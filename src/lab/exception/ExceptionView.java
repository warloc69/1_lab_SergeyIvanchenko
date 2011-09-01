package lab.exception;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
public class ExceptionView extends JFrame{
    public static final long serialVersionUID = 1233152332l;
	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ExceptionView.class);
	public ExceptionView (String info) {
		viewMassage(info, this);
		log.error(info);
	}
	public ExceptionView (String info, Object obj) {
		log.debug(info);
		viewMassage(info,obj);
	}
	private void viewMassage(final String info, Object obj) {		
		Box boxName = Box.createHorizontalBox();
		Box boxButton = Box.createHorizontalBox();
		Box allBoxes = Box.createVerticalBox(); 
		Container msg = getContentPane();
		JDialog dial = new JDialog((JDialog)obj, true);
		dial.setTitle("Critical error!!!");
        dial.setSize(300,100);
        msg.setBounds(0,0,300,100);
        addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                   if (info.charAt(0) == '1') {
					dispose();
					System.exit(0);
				   }
				   dispose();
                }
            }
        );
        //----- JLabel name
        final JLabel lname = new JLabel("info:");
        boxName.add(lname);
        //----- JTextField
        final JTextField tname = new JTextField();
		tname.setText(info);
        tname.setMaximumSize(new Dimension(250,30));        
        boxName.add(tname);
        tname.setEnabled(false);
		JButton ok = new JButton("Ok");
		ok.setMaximumSize(new Dimension(50,30));
        ok.addActionListener(
        new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (info.charAt(0) == '1') {
					dispose();
					System.exit(0);
				   }
				   dispose();
            }
        });
        boxButton.add(ok);
		allBoxes.add(boxName);
        allBoxes.add(boxButton);
        msg.add(allBoxes);
		dial.add(msg);
        dial.setVisible(true);
		dial.toFront();
    }
    
}
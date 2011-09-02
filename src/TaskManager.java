import lab.model.*;
import lab.view.*;
import lab.conntroller.*;
import lab.*;
import lab.exception.*;
public class TaskManager{
    public static void main (String[] arrg){  
        org.apache.log4j.BasicConfigurator.configure();
        ManagerController cont = new ManagerController();
        ManagerView view = new ManagerView();
		try {
			ManagerModel model = new ManagerModel();
			cont.setModel(model);
			view.setController(cont);
			model.addObserver(view);
		} catch (DataAccessException e) {
			e.printStackTrace();
			System.exit(0);
		}
        
    }
}
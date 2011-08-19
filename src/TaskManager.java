import lab.model.*;
import lab.view.*;
import lab.conntroller.*;
import lab.*;
public class TaskManager{
    public static void main (String[] arrg){  
        ManagerController cont = new ManagerController();
        ManagerView view = new ManagerView();
        ManagerModel model = new ManagerModel();
        cont.setModel(model);
        view.setController(cont);
        model.addObserver(view);
    }
}
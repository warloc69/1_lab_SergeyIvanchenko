package lab.model.observer;
import lab.model.*;
/**
* list metod for change info in the observer.
*/
public interface Observable {
    /**
    * update info in observer if task remove.
    */
    public void notifyRemove(long id);
    /**
    * update info in observer if task edit.
    */
    public void notifyEdit(long id);
    /**
    * update info in observer if task add.
    */
    public void notifyAdd(long id);
    /**
    * update all info in observer.
    */
    public void notifyGetAll(ModelGetInf inf);
}
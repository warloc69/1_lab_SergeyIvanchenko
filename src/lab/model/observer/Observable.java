package lab.model.observer;
import lab.model.*;
/**
* list metod for change info in the observer.
*/
public interface Observable {
    /**
    * update info in observer if task remove.
    * @param id id for the removes task.
    */
    public void notifyRemove(long id);
    /**
    * update info in observer if task edit.
    * @param id id for the edits task.
    */
    public void notifyEdit(long id);
    /**
    * update info in observer if task add.
    * @param id id for the adds task.
    */
    public void notifyAdd(long id);
    /**
    * update all info in observer.
    * @param inf reference of the ModelGetInf interface
    */
    public void notifyGetAll(ModelGetInf inf);
}
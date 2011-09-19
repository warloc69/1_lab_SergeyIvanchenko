package lab.model.observer;
/**
* list observer method.
*/
public interface Observer {    
    /**
    * add observer
    * @param view Observer adding.
    */
    public void addObserver(lab.model.observer.Observable view);
    /**
    * remove observer
    * @param view Observer removing.
    */
    public void removeObserver(lab.model.observer.Observable view);
}
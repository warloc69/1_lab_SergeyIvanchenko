package lab.model.observer;
/**
* list observer method.
*/
public interface Observer {    
    /**
    * add observer
    */
    public void addObserver(lab.model.observer.Observable view);
    /**
    * remove observer
    */
    public void removeObserver(lab.model.observer.Observable view);
}
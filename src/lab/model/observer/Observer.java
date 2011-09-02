package lab.model.observer;

public interface Observer {	
    public void addObserver(lab.model.observer.Observable view);
    public void removeObserver(lab.model.observer.Observable view);
}
package lab.model.observer;
import lab.model.*;
public interface Observable {
	public void notifyRemove(long id);
	public void notifyEdit(long id);
	public void notifyAdd(long id);
	public void notifyGetAll(ModelGetInf inf);
}
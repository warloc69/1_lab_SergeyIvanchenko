package lab.exception;
/**
*    Class create exception BadTaskDataException.
*    This exception calls, when the info in the task is wronge.
*/
public class BadTaskDateException extends RuntimeException {
	public static final long serialVersionUID = 12331231322l;
    public BadTaskDateException() {
        super();
    }
    public BadTaskDateException(String info) {
        super(info);
    }
	public BadTaskDateException(String message, Throwable cause) {
        super (message, cause);
	}
	public BadTaskDateException(Throwable cause) {
        super(cause);
    }
}
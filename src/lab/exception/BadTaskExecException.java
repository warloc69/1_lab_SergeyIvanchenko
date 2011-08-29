package lab.exception;
/**
*    Class create exception BadTaskExecException.
*    This exception calls, when the info in the task is wronge.
*/
public class BadTaskExecException extends RuntimeException {
	public static final long serialVersionUID = 1233123322l;
    public BadTaskExecException() {
        super();
    }
    public BadTaskExecException(String info) {
        super(info);
    }
	public BadTaskExecException(String message, Throwable cause) {
        super (message, cause);
	}
	public BadTaskExecException(Throwable cause) {
        super(cause);
    }
}
package lab.exception;
/**
*    Class create exception BDException.
*    This exception calls, when we can't work with Data Base.
*/
public class BDException extends RuntimeException {
	public static final long serialVersionUID = 12331233224l;
    public BDException() {
        super();
    }
    public BDException(String info) {
        super(info);
    }
	public BDException(String message, Throwable cause) {
        super (message, cause);
	}
	public BDException(Throwable cause) {
        super(cause);
    }
}
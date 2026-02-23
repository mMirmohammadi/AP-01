package exception;

public class NotImplementedException extends Exception {
    private static final long serialVersionUID = 1337773604834974843L;

    public NotImplementedException(String message) {
        super(message);
    }
}

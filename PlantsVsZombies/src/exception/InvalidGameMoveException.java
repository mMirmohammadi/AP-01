package exception;

public class InvalidGameMoveException extends Exception {
    private static final long serialVersionUID = -958383383950487951L;

    public InvalidGameMoveException(String message) {
        super(message);
    }
}

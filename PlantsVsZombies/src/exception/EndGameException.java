package exception;

public class EndGameException extends Exception {
    private static final long serialVersionUID = -6003244006225470635L;
    private final Winner winner;
    private final boolean aborted;

    public EndGameException(Winner winner) {
        this.winner = winner;
        this.aborted = false;
    }

    public EndGameException() {
        this.winner = null;
        this.aborted = true;
    }

    public Winner getWinner() {
        return winner;
    }

    public boolean isAborted() {
        return aborted;
    }
}

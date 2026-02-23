package line;

import exception.InvalidGameMoveException;

public class Line {

    private final Integer lineNumber;
    private final LineState lineState;
    private LawnMower lawnMower;

    public Line(Integer lineNumber, LineState lineState, LawnMower lawnMower) {
        this.lineNumber = lineNumber;
        this.lineState = lineState;
        this.lawnMower = lawnMower;
    }

    public void activateLawnMower() throws InvalidGameMoveException {
        if (lawnMower == null) {
            throw new InvalidGameMoveException(String.format("Line number %d does not contain a lawnmower", lineNumber));
        }
        lawnMower.activate();
        lawnMower = null;
    }

    public boolean lawnMower() {
        return lawnMower != null;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public LineState getLineState() {
        return lineState;
    }
}

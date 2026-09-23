import java.util.Map;

public class Transition {
    private Map<Position, Position> moves;

    public Transition(Map<Position, Position> moves) {
        this.moves = moves;
    }

    public Transition() {
    }

    public Map<Position, Position> getMoves() {
        return moves;
    }

    public void setMoves(Map<Position, Position> moves) {
        this.moves = moves;
    }
}

import java.util.HashMap;
import java.util.Map;

public class GameStateTransition {
    private Map<Position, Position> moves;

    public GameStateTransition() {
    }

    public GameStateTransition(Map<Position, Position> moves) {
        this.moves = moves;
    }

    public void add(Position from, Position to) {
        Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
        this.moves.put(key, to);
    }

    public GameStateTransition reverse() {
        Map<Position, Position> moves = this.moves.entrySet().stream().collect(java.util.stream.Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return new GameStateTransition(moves);
    }

    public Map<Position, Position> getMoves() {
        return moves;
    }

    public void setMoves(Map<Position, Position> moves) {
        this.moves = moves;
    }
}

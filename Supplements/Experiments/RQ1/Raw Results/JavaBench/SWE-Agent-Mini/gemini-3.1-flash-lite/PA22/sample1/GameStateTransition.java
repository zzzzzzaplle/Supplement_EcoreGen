import java.util.*;
import java.util.stream.*;

public class GameStateTransition {
    private Map<Position, Position> moves = new HashMap<>();

    public GameStateTransition() {}

    public void add(Position from, Position to) {
        final Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
        this.moves.put(key, to);
    }
    public GameStateTransition reverse() {
        final Map<Position, Position> moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return new GameStateTransition(moves);
    }

    public GameStateTransition(Map<Position, Position> moves) {
        this.moves = moves;
    }

    public Map<Position, Position> getMoves() { return moves; }
    public void setMoves(Map<Position, Position> moves) { this.moves = moves; }
}

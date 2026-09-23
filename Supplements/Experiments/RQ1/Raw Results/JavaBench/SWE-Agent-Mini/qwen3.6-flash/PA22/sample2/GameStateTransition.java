
import java.util.*;

/**
 * Represents a transition of game state changes.
 */
public class GameStateTransition {
    protected Map<Position, Position> moves;

    public GameStateTransition() {
        this.moves = new HashMap<>();
    }

    public GameStateTransition(Map<Position, Position> moves) {
        this.moves = new HashMap<>(moves);
    }

    public void add(Position from, Position to) {
        Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
        this.moves.put(key, to);
    }

    public GameStateTransition reverse() {
        Map<Position, Position> moves = new HashMap<>();
        for (Map.Entry<Position, Position> entry : this.moves.entrySet()) {
            moves.put(entry.getValue(), entry.getKey());
        }
        return new GameStateTransition(moves);
    }

    public Map<Position, Position> getMovesCopy() {
        return new HashMap<>(moves);
    }
}

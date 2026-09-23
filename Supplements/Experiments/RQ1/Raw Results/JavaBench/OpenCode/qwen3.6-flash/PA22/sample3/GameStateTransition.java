import java.util.*;
import java.util.stream.*;

public class GameStateTransition {
    private Map<Position, Position> moves;

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
        Map<Position, Position> moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return new Transition(moves);
    }

    public Map<Position, Position> getMoves() {
        return new HashMap<>(this.moves);
    }

    public void setMoves(Map<Position, Position> moves) {
        this.moves = new HashMap<>(moves);
    }
}

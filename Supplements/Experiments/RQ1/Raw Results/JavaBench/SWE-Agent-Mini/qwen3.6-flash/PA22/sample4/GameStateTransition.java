import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class GameStateTransition {
    private Map<Position, Position> moves;

    public GameStateTransition(Map<Position, Position> moves) {
        this.moves = new HashMap<>(moves);
    }

    public GameStateTransition() {
        this.moves = new HashMap<>();
    }

    public void add(Position from, Position to) {
        final Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(Map.Entry::getKey)
                .findFirst().orElse(from);
        this.moves.put(key, to);
    }

    public Map<Position, Position> reverse() {
        final Map<Position, Position> moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        return moves;
    }

    public Map<Position, Position> getMoves() {
        return moves;
    }
}

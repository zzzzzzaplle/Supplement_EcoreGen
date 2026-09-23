import java.util.Map;
import java.util.stream.Collectors;

class GameStateTransition {

    private Map<Position, Position> moves;

    GameStateTransition() {
        this.moves = new java.util.HashMap<>();
    }

    GameStateTransition(Map<Position, Position> moves) {
        this.moves = new java.util.HashMap<>(moves);
    }

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
}

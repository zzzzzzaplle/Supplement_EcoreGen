import java.util.HashMap;
import java.util.Map;

public class GameStateTransition {
    private Map<Position, Position> moves;

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
        public Transition reverse() {
            final Map<Position, Position> moves = this.moves.entrySet().stream().collect(java.util.stream.Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            return new Transition(moves);
        }

    public Map<Position, Position> getMoves() {
        return moves;
    }

    public void setMoves(Map<Position, Position> moves) {
        this.moves = moves;
    }

    public static class Transition extends GameStateTransition {
        public Transition() {
        }

        public Transition(Map<Position, Position> moves) {
            setMoves(moves);
        }
    }
}

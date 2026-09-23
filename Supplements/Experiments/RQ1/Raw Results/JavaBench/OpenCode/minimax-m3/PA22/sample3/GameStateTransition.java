import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GameStateTransition {
    private Map<Position, Position> moves;

    public GameStateTransition() {
        this.moves = new HashMap<>();
    }

    public GameStateTransition(Map<Position, Position> moves) {
        this.moves = new HashMap<>(moves);
    }

    public Map<Position, Position> getMoves() {
        return moves;
    }

    public void setMoves(Map<Position, Position> moves) {
        this.moves = moves;
    }

        public void add(Position from, Position to) {
            final Position key = this.moves.entrySet().stream()
                    .filter(e -> e.getValue().equals(from))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(from);
            this.moves.put(key, to);
        }
        public Transition reverse() {
            final Map<Position, Position> moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            return new Transition(moves);
        }

    public static class Transition {
        private Map<Position, Position> moves;

        public Transition() {
            this.moves = new HashMap<>();
        }

        public Transition(Map<Position, Position> moves) {
            this.moves = moves;
        }

        public Map<Position, Position> getMoves() {
            return moves;
        }

        public void setMoves(Map<Position, Position> moves) {
            this.moves = moves;
        }
    }
}

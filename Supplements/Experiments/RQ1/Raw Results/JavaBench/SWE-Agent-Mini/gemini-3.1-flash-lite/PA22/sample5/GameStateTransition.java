import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

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
    public GameStateTransition reverse() {
            final Map<Position, Position> moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            GameStateTransition reversed = new GameStateTransition();
            reversed.setMoves(moves);
            return reversed;
    }

    public Map<Position, Position> getMoves() { return moves; }
    public void setMoves(Map<Position, Position> moves) { this.moves = moves; }
}

import java.util.*;
import java.util.stream.Collectors;

public class GameStateTransition {
    private Map<Position, Position> moves;

    public GameStateTransition() {}
    public Map<Position, Position> getMoves() { return moves; }
    public void setMoves(Map<Position, Position> moves) { this.moves = moves; }

        public void add(Position from, Position to) {
            final Position key = this.moves.entrySet().stream()
                    .filter(e -> e.getValue().equals(from))
                    .map(Map.Entry::getKey)
                    .findFirst().orElse(from);
            this.moves.put(key, to);
        }
        public GameStateTransition reverse() {
            final Map<Position, Position> moves = this.moves.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            GameStateTransition reverse = new GameStateTransition();
            reverse.setMoves(moves);
            return reverse;
        }
}

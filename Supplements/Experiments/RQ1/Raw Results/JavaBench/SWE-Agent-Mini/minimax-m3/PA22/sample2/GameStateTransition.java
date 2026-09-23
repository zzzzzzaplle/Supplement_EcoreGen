import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GameStateTransition {
    private Map<Position, Position> moves;

    public GameStateTransition() {
        this.moves = new LinkedHashMap<>();
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

    public GameStateTransition reverse() {
        final Map<Position, Position> reversed = this.moves.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (a, b) -> a, LinkedHashMap::new));
        GameStateTransition result = new GameStateTransition();
        result.setMoves(reversed);
        return result;
    }
}

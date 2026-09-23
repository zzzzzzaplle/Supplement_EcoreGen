public class GameStateTransition {
    private java.util.Map<Position, Position> moves = new java.util.HashMap<>();

    public GameStateTransition() {}

    public void add(Position from, Position to) {
        final Position key = this.moves.entrySet().stream()
                .filter(e -> e.getValue().equals(from))
                .map(java.util.Map.Entry::getKey)
                .findFirst().orElse(from);
        this.moves.put(key, to);
    }

    public GameStateTransition reverse() {
        final java.util.Map<Position, Position> moves = this.moves.entrySet().stream().collect(java.util.stream.Collectors.toMap(java.util.Map.Entry::getValue, java.util.Map.Entry::getKey));
        return new GameStateTransition(moves);
    }
    
    private GameStateTransition(java.util.Map<Position, Position> moves) {
        this.moves = moves;
    }

    public java.util.Map<Position, Position> getMoves() { return moves; }
    public void setMoves(java.util.Map<Position, Position> moves) { this.moves = moves; }
}

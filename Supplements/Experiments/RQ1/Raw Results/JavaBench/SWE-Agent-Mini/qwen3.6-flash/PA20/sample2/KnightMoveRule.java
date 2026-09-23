public class KnightMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Place source = move.getSource();
        Place dest = move.getDestination();
        
        int dx = Math.abs(dest.x() - source.x());
        int dy = Math.abs(dest.y() - source.y());
        
        return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
    
    public KnightMoveRule() {
    }
}

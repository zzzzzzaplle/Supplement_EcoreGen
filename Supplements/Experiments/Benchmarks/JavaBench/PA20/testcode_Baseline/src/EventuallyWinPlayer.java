public class EventuallyWinPlayer extends MockPlayer {
    public EventuallyWinPlayer(Color color) {
        super(color);
    }

    @Override
    public Move nextMove(Game game, Move[] availableMoves) {
        var minDistance = Integer.MAX_VALUE;
        var best = availableMoves[0];
        for (var move :
                availableMoves) {
            var dist = Math.abs(move.getDestination().getX() - move.getSource().getX()) +
                    Math.abs(move.getDestination().getY() - move.getSource().getY());
            if (dist <= minDistance) {
                minDistance = dist;
                best = move;
            }
        }
        return best;
    }
}

class KnightMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        if (!(sourcePiece instanceof Knight)) {
            return true;
        }
        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

public String getDescription() {
        return "knight move rule is violated";
    }
}

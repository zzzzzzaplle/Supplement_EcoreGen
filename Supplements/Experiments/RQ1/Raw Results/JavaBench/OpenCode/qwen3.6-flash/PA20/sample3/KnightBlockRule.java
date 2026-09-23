class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        if (!(sourcePiece instanceof Knight)) {
            return true;
        }
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();
        Place blockedPlace;
        if (Math.abs(dx) == 2) {
            blockedPlace = new Place(move.getSource().x() + dx / 2, move.getSource().y());
        } else {
            blockedPlace = new Place(move.getSource().x(), move.getSource().y() + dy / 2);
        }
        return game.getPiece(blockedPlace) == null;
    }

public String getDescription() {
        return "knight is blocked by another piece";
    }
}

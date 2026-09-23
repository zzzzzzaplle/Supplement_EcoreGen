public class KnightBlockRule implements Rule {
    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        int dx = move.getDestination().getX() - move.getSource().getX();
        int dy = move.getDestination().getY() - move.getSource().getY();
        Place block = new Place(move.getSource().getX(), move.getSource().getY());
        if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
            block.setX(move.getSource().getX() + dx / 2);
        } else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
            block.setY(move.getSource().getY() + dy / 2);
        }
        return game.getPiece(block) == null;
    }

    public String getDescription() {
        return "knight is blocked by another piece";
    }
}

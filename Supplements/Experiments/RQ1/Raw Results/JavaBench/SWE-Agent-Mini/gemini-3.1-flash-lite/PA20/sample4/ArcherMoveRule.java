public class ArcherMoveRule implements Rule {
    public ArcherMoveRule() {}
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        return false;
    }
    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}

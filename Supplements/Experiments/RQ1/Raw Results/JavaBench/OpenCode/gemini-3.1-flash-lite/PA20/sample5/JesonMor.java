public class JesonMor extends Game {
    public JesonMor() {}
    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
    }
    @Override
    public Player start() { return null; }
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) { return null; }
    @Override
    public void updateScore(Player player, Piece piece, Move move) {}
    @Override
    public void movePiece(Move move) {}
    @Override
    public Move[] getAvailableMoves(Player player) { return null; }
}
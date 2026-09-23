public class JesonMor extends Game {
    public JesonMor() { super(); }
    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.board = configuration.getInitialBoard();
        this.numMoves = 0;
        this.currentPlayer = configuration.getPlayers()[0];
    }
    public Player start() { return null; }
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) { return null; }
    public void updateScore(Player player, Piece piece, Move move) {}
    public void movePiece(Move move) {}
    public Move[] getAvailableMoves(Player player) { return null; }
}

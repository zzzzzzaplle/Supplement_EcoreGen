public class JesonMor extends Game {
    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.board = new Piece[configuration.getSize()][configuration.getSize()];
        for (int x = 0; x < configuration.getSize(); x++) {
            System.arraycopy(configuration.getInitialBoard()[x], 0, this.board[x], 0, configuration.getSize());
        }
        this.numMoves = 0;
        this.currentPlayer = configuration.getPlayers()[0];
    }

    @Override
    public Player start() {
        return null;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
    }

    @Override
    public void movePiece(Move move) {
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        return new Move[0];
    }
}

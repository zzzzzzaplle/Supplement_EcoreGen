public class JesonMor extends Game {
    public JesonMor() {
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.board = configuration.getInitialBoard();
        this.currentPlayer = configuration.getPlayers()[0];
    }

    @Override
    public Player start() {
        return this.currentPlayer;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        return lastPlayer;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getDestination().getX() - move.getSource().getX()) + Math.abs(move.getDestination().getY() - move.getSource().getY());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = this.getPiece(move.getSource());
        this.board[move.getDestination().getX()][move.getDestination().getY()] = piece;
        this.board[move.getSource().getX()][move.getSource().getY()] = null;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        return new Move[0];
    }
}

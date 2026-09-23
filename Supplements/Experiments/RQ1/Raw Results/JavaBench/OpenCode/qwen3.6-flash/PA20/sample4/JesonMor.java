import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {

    public JesonMor() {
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.board = new Piece[configuration.getSize()][configuration.getSize()];
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                this.board[x][y] = configuration.getInitialBoard()[x][y];
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.numMoves = 0;
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

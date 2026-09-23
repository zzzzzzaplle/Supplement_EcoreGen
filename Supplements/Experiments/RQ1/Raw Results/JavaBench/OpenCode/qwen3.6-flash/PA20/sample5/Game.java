import java.util.ArrayList;
import java.util.List;

public abstract class Game implements Cloneable {
    protected Configuration configuration;
    protected Piece[][] board;
    protected Player currentPlayer;
    protected int numMoves;

    public Game() {
    }

    public abstract Player start();

    public abstract Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove);

    public abstract void updateScore(Player player, Piece piece, Move move);

    public abstract void movePiece(Move move);

    public abstract Move[] getAvailableMoves(Player player);

    public abstract Piece getPiece(Place place);

    public abstract Piece getPiece(int x, int y);

    public abstract void refreshOutput();

    public Game clone() throws CloneNotSupportedException {
        return (Game) super.clone();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    public Place getCentralPlace() {
        return configuration.getCentralPlace();
    }

    protected int configurationGetSize() {
        return configuration.getSize();
    }
}

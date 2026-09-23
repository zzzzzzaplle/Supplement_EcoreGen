import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {
    public JesonMor() {
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        int size = configuration.getSize();
        this.board = new Piece[size][size];
        Piece[][] initialBoard = configuration.getInitialBoard();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                this.board[x][y] = initialBoard[x][y];
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.numMoves = 0;
    }

    @Override
    public Player start() {
        while (true) {
            refreshOutput();
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves.length == 0) {
                Player[] players = configuration.getPlayers();
                Player other = players[0].equals(currentPlayer) ? players[1] : players[0];
                if (currentPlayer.getScore() > other.getScore()) {
                    return currentPlayer;
                } else if (other.getScore() > currentPlayer.getScore()) {
                    return other;
                } else {
                    return currentPlayer;
                }
            }
            Move move = currentPlayer.nextMove(this, availableMoves);
            if (move == null) {
                Player[] players = configuration.getPlayers();
                Player other = players[0].equals(currentPlayer) ? players[1] : players[0];
                if (currentPlayer.getScore() > other.getScore()) {
                    return currentPlayer;
                } else if (other.getScore() > currentPlayer.getScore()) {
                    return other;
                } else {
                    return currentPlayer;
                }
            }
            movePiece(move);
            Piece piece = getPiece(move.getDestination());
            updateScore(currentPlayer, piece, move);
            this.numMoves++;
            Player winner = getWinner(currentPlayer, piece, move);
            if (winner != null) {
                refreshOutput();
                return winner;
            }
            Player[] players = configuration.getPlayers();
            currentPlayer = players[0].equals(currentPlayer) ? players[1] : players[0];
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }
        Place src = lastMove.getSource();
        Place dst = lastMove.getDestination();
        Place central = configuration.getCentralPlace();
        if (lastPiece instanceof Knight && src.equals(central) && !dst.equals(central)) {
            return lastPlayer;
        }
        int size = configuration.getSize();
        Player[] players = configuration.getPlayers();
        Player other = players[0].equals(lastPlayer) ? players[1] : players[0];
        boolean otherHasPieces = false;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = getPiece(x, y);
                if (p != null && p.getPlayer().equals(other)) {
                    otherHasPieces = true;
                    break;
                }
            }
            if (otherHasPieces) break;
        }
        if (!otherHasPieces) {
            return lastPlayer;
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getDestination().x() - move.getSource().x()) +
                Math.abs(move.getDestination().y() - move.getSource().y());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Place src = move.getSource();
        Place dst = move.getDestination();
        Piece piece = getPiece(src);
        setPiece(src, null);
        setPiece(dst, piece);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        int size = configuration.getSize();
        List<Move> allMoves = new ArrayList<>();
        FirstNMovesProtectionRule protectionRule = new FirstNMovesProtectionRule(configuration.getNumMovesProtection());
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Move[] pieceMoves = piece.getAvailableMoves(this, new Place(x, y));
                    for (Move move : pieceMoves) {
                        if (protectionRule.validate(this, move)) {
                            allMoves.add(move);
                        }
                    }
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }
}

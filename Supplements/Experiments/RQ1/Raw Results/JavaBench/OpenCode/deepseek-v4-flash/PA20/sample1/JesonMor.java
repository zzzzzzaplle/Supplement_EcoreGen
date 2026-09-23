import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {

    public JesonMor() {
    }

    public JesonMor(Configuration configuration) {
        super(configuration);
    }

    @Override
    public Player start() {
        while (true) {
            refreshOutput();
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves.length == 0) {
                Player winner;
                Player[] players = configuration.getPlayers();
                if (players[0].getScore() > players[1].getScore()) {
                    winner = players[0];
                } else if (players[1].getScore() > players[0].getScore()) {
                    winner = players[1];
                } else {
                    winner = currentPlayer;
                }
                return winner;
            }
            Move move = currentPlayer.nextMove(this, availableMoves);
            if (move == null) {
                continue;
            }
            Piece piece = getPiece(move.getSource());
            movePiece(move);
            numMoves++;
            updateScore(currentPlayer, piece, move);
            Player winner = getWinner(currentPlayer, piece, move);
            if (winner != null) {
                refreshOutput();
                return winner;
            }
            currentPlayer = (currentPlayer.equals(configuration.getPlayers()[0]))
                    ? configuration.getPlayers()[1]
                    : configuration.getPlayers()[0];
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (numMoves <= configuration.getNumMovesProtection()) {
            return null;
        }
        Place central = configuration.getCentralPlace();
        if (lastPiece instanceof Knight && lastMove.getSource().equals(central) && !lastMove.getDestination().equals(central)) {
            return lastPlayer;
        }
        int size = configuration.getSize();
        int player0Count = 0;
        int player1Count = 0;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = getPiece(x, y);
                if (p != null) {
                    if (p.getPlayer().equals(configuration.getPlayers()[0])) {
                        player0Count++;
                    } else {
                        player1Count++;
                    }
                }
            }
        }
        if (player0Count == 0) {
            return configuration.getPlayers()[1];
        }
        if (player1Count == 0) {
            return configuration.getPlayers()[0];
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getSource().x() - move.getDestination().x())
                + Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = getPiece(move.getSource());
        setPiece(move.getSource(), null);
        setPiece(move.getDestination(), piece);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> allMoves = new ArrayList<>();
        int size = configuration.getSize();
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

import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {
    private Rule[] rules;

    public JesonMor() {
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.numMoves = 0;
        this.board = new Piece[configuration.getSize()][configuration.getSize()];
        // copy initial board
        for (int x = 0; x < configuration.getSize(); x++) {
            System.arraycopy(configuration.getInitialBoard()[x], 0, this.board[x], 0, configuration.getSize());
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.rules = new Rule[]{
                new OutOfBoundaryRule(),
                new VacantRule(),
                new NilMoveRule(),
                new OccupiedRule(),
                new FirstNMovesProtectionRule(configuration.getNumMovesProtection()),
                new KnightMoveRule(),
                new KnightBlockRule(),
                new ArcherMoveRule()
        };
    }

    public Rule[] getRules() {
        return rules;
    }

    public void setRules(Rule[] rules) {
        this.rules = rules;
    }

    @Override
    public Player start() {
        while (true) {
            refreshOutput();
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            if (availableMoves.length == 0) {
                Player[] players = configuration.getPlayers();
                Player otherPlayer = players[0].equals(currentPlayer) ? players[1] : players[0];
                if (currentPlayer.getScore() > otherPlayer.getScore()) {
                    return currentPlayer;
                } else if (otherPlayer.getScore() > currentPlayer.getScore()) {
                    return otherPlayer;
                } else {
                    return currentPlayer;
                }
            }
            Move move = currentPlayer.nextMove(this, availableMoves);
            if (move == null) {
                continue;
            }
            if (validateMove(move)) {
                Piece piece = getPiece(move.getSource());
                movePiece(move);
                numMoves++;
                updateScore(currentPlayer, piece, move);
                Player winner = getWinner(currentPlayer, piece, move);
                if (winner != null) {
                    refreshOutput();
                    return winner;
                }
                Player[] players = configuration.getPlayers();
                currentPlayer = players[0].equals(currentPlayer) ? players[1] : players[0];
            }
        }
    }

    private boolean validateMove(Move move) {
        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (numMoves < configuration.getNumMovesProtection()) {
            return null;
        }
        Place centralPlace = configuration.getCentralPlace();
        if (lastPiece instanceof Knight && lastMove.getSource().equals(centralPlace) && !lastMove.getDestination().equals(centralPlace)) {
            return lastPlayer;
        }
        int count = 0;
        Player lastPlayerFound = null;
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece p = getPiece(x, y);
                if (p != null) {
                    count++;
                    lastPlayerFound = p.getPlayer();
                }
            }
        }
        if (count == 1) {
            return lastPlayerFound;
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
        Piece piece = getPiece(move.getSource());
        board[move.getSource().x()][move.getSource().y()] = null;
        board[move.getDestination().x()][move.getDestination().y()] = piece;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> moves = new ArrayList<>();
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place source = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, source);
                    for (Move m : pieceMoves) {
                        if (validateMove(m)) {
                            moves.add(m);
                        }
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}

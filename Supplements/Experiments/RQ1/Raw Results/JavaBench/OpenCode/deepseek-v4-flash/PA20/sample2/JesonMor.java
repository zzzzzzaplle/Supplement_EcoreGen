import java.util.ArrayList;
import java.util.Arrays;

public class JesonMor extends Game {
    public JesonMor() {
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        int size = configuration.getSize();
        this.board = new Piece[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                this.board[x][y] = configuration.getInitialBoard()[x][y];
            }
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.numMoves = 0;
    }

    @Override
    public Player start() {
        while (true) {
            refreshOutput();
            Move[] availableMoves = getAvailableMoves(this.currentPlayer);
            if (availableMoves.length == 0) {
                Player opponent = getOpponent(this.currentPlayer);
                if (this.currentPlayer.getScore() > opponent.getScore()) {
                    return this.currentPlayer;
                } else if (opponent.getScore() > this.currentPlayer.getScore()) {
                    return opponent;
                } else {
                    return this.currentPlayer;
                }
            }
            Move move = this.currentPlayer.nextMove(this, availableMoves);
            if (move == null) {
                return getOpponent(this.currentPlayer);
            }
            Piece piece = getPiece(move.getSource());
            movePiece(move);
            this.numMoves++;
            updateScore(this.currentPlayer, piece, move);
            Player winner = getWinner(this.currentPlayer, piece, move);
            if (winner != null) {
                refreshOutput();
                return winner;
            }
            this.currentPlayer = getOpponent(this.currentPlayer);
        }
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }
        if (lastPiece instanceof Knight && lastMove.getSource().equals(this.configuration.getCentralPlace())
                && !lastMove.getDestination().equals(this.configuration.getCentralPlace())) {
            return lastPlayer;
        }
        Player opponent = getOpponent(lastPlayer);
        boolean opponentHasPieces = false;
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = this.board[x][y];
                if (p != null && p.getPlayer().equals(opponent)) {
                    opponentHasPieces = true;
                    break;
                }
            }
            if (opponentHasPieces) break;
        }
        if (!opponentHasPieces) {
            return lastPlayer;
        }
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getSource().x() - move.getDestination().x()) +
                Math.abs(move.getSource().y() - move.getDestination().y());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = getPiece(move.getSource());
        setPiece(move.getDestination(), piece);
        setPiece(move.getSource(), null);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> validMoves = new ArrayList<Move>();
        int size = this.configuration.getSize();
        Rule[] rules = getRules();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place source = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, source);
                    for (Move move : pieceMoves) {
                        boolean allValid = true;
                        for (Rule rule : rules) {
                            if (!rule.validate(this, move)) {
                                allValid = false;
                                break;
                            }
                        }
                        if (allValid) {
                            validMoves.add(move);
                        }
                    }
                }
            }
        }
        return validMoves.toArray(new Move[0]);
    }

    private Rule[] getRules() {
        return new Rule[] {
            new VacantRule(),
            new OccupiedRule(),
            new OutOfBoundaryRule(),
            new NilMoveRule(),
            new FirstNMovesProtectionRule(this.configuration.getNumMovesProtection()),
            new KnightMoveRule(),
            new KnightBlockRule(),
            new ArcherMoveRule()
        };
    }

    private Player getOpponent(Player player) {
        for (Player p : this.configuration.getPlayers()) {
            if (!p.equals(player)) {
                return p;
            }
        }
        return null;
    }
}

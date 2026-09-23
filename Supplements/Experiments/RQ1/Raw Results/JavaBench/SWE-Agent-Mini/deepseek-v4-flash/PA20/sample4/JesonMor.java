import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {

    public JesonMor() {
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.board = new Piece[configuration.getSize()][configuration.getSize()];
        // Copy initial board
        Piece[][] initial = configuration.getInitialBoard();
        for (int x = 0; x < configuration.getSize(); x++) {
            System.arraycopy(initial[x], 0, this.board[x], 0, configuration.getSize());
        }
        this.currentPlayer = configuration.getPlayers()[0];
        this.numMoves = 0;
    }

    @Override
    public Player start() {
        Player winner = null;
        while (winner == null) {
            refreshOutput();

            // Get available moves for current player
            Move[] availableMoves = getAvailableMoves(currentPlayer);

            if (availableMoves == null || availableMoves.length == 0) {
                // No available moves - winner by score comparison
                Player[] players = configuration.getPlayers();
                Player otherPlayer = players[0].equals(currentPlayer) ? players[1] : players[0];
                if (currentPlayer.getScore() > otherPlayer.getScore()) {
                    winner = currentPlayer;
                } else if (otherPlayer.getScore() > currentPlayer.getScore()) {
                    winner = otherPlayer;
                } else {
                    winner = currentPlayer; // scores equal, current player wins
                }
                break;
            }

            // Ask player for a move
            Move move = currentPlayer.nextMove(this, availableMoves);

            if (move == null) {
                continue;
            }

            // Validate the move against all rules
            Rule[] rules = getAllRules();
            boolean valid = true;
            for (Rule rule : rules) {
                if (!rule.validate(this, move)) {
                    valid = false;
                    break;
                }
            }

            if (!valid) {
                continue;
            }

            // Execute the move
            Piece piece = getPiece(move.getSource());
            movePiece(move);
            updateScore(currentPlayer, piece, move);
            numMoves++;

            // Check win condition
            winner = getWinner(currentPlayer, piece, move);

            // Switch player
            if (winner == null) {
                Player[] players = configuration.getPlayers();
                currentPlayer = currentPlayer.equals(players[0]) ? players[1] : players[0];
            }
        }

        refreshOutput();
        System.out.println("Winner: " + winner.getName() + "!");
        return winner;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // No winner during protection phase
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }

        // Condition 1: A Knight leaves the central square
        Place central = configuration.getCentralPlace();
        if (lastPiece instanceof Knight &&
                lastMove.getSource().equals(central) &&
                !lastMove.getDestination().equals(central)) {
            return lastPlayer;
        }

        // Condition 2: Only one player's pieces remain
        Player[] players = configuration.getPlayers();
        Player otherPlayer = players[0].equals(lastPlayer) ? players[1] : players[0];
        boolean otherHasPiece = false;
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece p = getPiece(x, y);
                if (p != null && p.getPlayer().equals(otherPlayer)) {
                    otherHasPiece = true;
                    break;
                }
            }
            if (otherHasPiece) break;
        }
        if (!otherHasPiece) {
            return lastPlayer;
        }

        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getSource().getX() - move.getDestination().getX()) +
                Math.abs(move.getSource().getY() - move.getDestination().getY());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = getPiece(move.getSource());
        this.board[move.getSource().getX()][move.getSource().getY()] = null;
        this.board[move.getDestination().getX()][move.getDestination().getY()] = piece;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> allMoves = new ArrayList<>();
        for (int x = 0; x < configuration.getSize(); x++) {
            for (int y = 0; y < configuration.getSize(); y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Move[] pieceMoves = piece.getAvailableMoves(this, new Place(x, y));
                    if (pieceMoves != null) {
                        // Filter moves through all rules
                        for (Move move : pieceMoves) {
                            if (isMoveValid(move)) {
                                allMoves.add(move);
                            }
                        }
                    }
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }

    private boolean isMoveValid(Move move) {
        Rule[] rules = getAllRules();
        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        return true;
    }

    private Rule[] getAllRules() {
        List<Rule> rules = new ArrayList<>();
        rules.add(new OutOfBoundaryRule());
        rules.add(new VacantRule());
        rules.add(new NilMoveRule());
        rules.add(new OccupiedRule());
        rules.add(new FirstNMovesProtectionRule(configuration.getNumMovesProtection()));
        rules.add(new KnightMoveRule());
        rules.add(new KnightBlockRule());
        rules.add(new ArcherMoveRule());
        return rules.toArray(new Rule[0]);
    }
}

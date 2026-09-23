import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JesonMor extends Game {

    public JesonMor() {
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        // Initialize board from configuration's initialBoard
        int size = configuration.getSize();
        this.board = new Piece[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                this.board[x][y] = configuration.getInitialBoard()[x][y];
            }
        }
        this.numMoves = 0;
        this.currentPlayer = configuration.getPlayers()[0];
    }

    @Override
    public Player start() {
        Player winner = null;
        while (true) {
            this.refreshOutput();

            Player current = this.currentPlayer;
            Move[] availableMoves = getAvailableMoves(current);

            if (availableMoves.length == 0) {
                // No available moves - winner decided by score
                Player[] players = configuration.getPlayers();
                Player other = players[0] == current ? players[1] : players[0];
                if (other.getScore() > current.getScore()) {
                    winner = other;
                } else {
                    winner = current;
                }
                break;
            }

            // Get player's chosen move
            Move move = null;
            while (move == null) {
                move = current.nextMove(this, availableMoves);
                if (move == null) {
                    System.out.println("Invalid move. Try again.");
                }
            }

            // Execute the move
            Piece piece = getPiece(move.getSource());
            movePiece(move);
            updateScore(current, piece, move);
            this.numMoves++;

            // Check winner
            winner = getWinner(current, piece, move);
            if (winner != null) {
                this.refreshOutput();
                break;
            }

            // Switch player
            Player[] players = configuration.getPlayers();
            this.currentPlayer = (current == players[0]) ? players[1] : players[0];
        }
        return winner;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // No winner during protection phase
        if (this.numMoves < configuration.getNumMovesProtection()) {
            return null;
        }

        // Condition 1: A Knight leaves the central square
        Place centralPlace = configuration.getCentralPlace();
        if (lastPiece instanceof Knight && lastMove.getSource().equals(centralPlace) && !lastMove.getDestination().equals(centralPlace)) {
            return lastPlayer;
        }

        // Condition 2: Only one player's pieces remain on the board
        Player[] players = configuration.getPlayers();
        int size = configuration.getSize();
        boolean[] playerHasPieces = new boolean[]{false, false};
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = getPiece(x, y);
                if (p != null) {
                    if (p.getPlayer().equals(players[0])) {
                        playerHasPieces[0] = true;
                    } else if (p.getPlayer().equals(players[1])) {
                        playerHasPieces[1] = true;
                    }
                }
            }
        }
        if (playerHasPieces[0] && !playerHasPieces[1]) {
            return players[0];
        }
        if (!playerHasPieces[0] && playerHasPieces[1]) {
            return players[1];
        }

        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int manhattanDistance = Math.abs(move.getSource().getX() - move.getDestination().getX()) +
                                Math.abs(move.getSource().getY() - move.getDestination().getY());
        player.setScore(player.getScore() + manhattanDistance);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = getPiece(move.getSource());
        setPiece(move.getDestination(), piece);
        setPiece(move.getSource(), null);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        int size = configuration.getSize();
        List<Move> allMoves = new ArrayList<>();

        // Collect all rules
        List<Rule> globalRules = Arrays.asList(
            new VacantRule(),
            new OccupiedRule(),
            new OutOfBoundaryRule(),
            new NilMoveRule(),
            new FirstNMovesProtectionRule(configuration.getNumMovesProtection())
        );

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place source = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, source);
                    for (Move move : pieceMoves) {
                        boolean valid = true;
                        // Apply global rules
                        for (Rule rule : globalRules) {
                            if (!rule.validate(this, move)) {
                                valid = false;
                                break;
                            }
                        }
                        if (!valid) continue;

                        // Apply piece-specific rules
                        if (piece instanceof Knight) {
                            Rule knightMoveRule = new KnightMoveRule();
                            Rule knightBlockRule = new KnightBlockRule();
                            if (!knightMoveRule.validate(this, move) || !knightBlockRule.validate(this, move)) {
                                continue;
                            }
                        } else if (piece instanceof Archer) {
                            Rule archerMoveRule = new ArcherMoveRule();
                            if (!archerMoveRule.validate(this, move)) {
                                continue;
                            }
                        }

                        allMoves.add(move);
                    }
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }
}

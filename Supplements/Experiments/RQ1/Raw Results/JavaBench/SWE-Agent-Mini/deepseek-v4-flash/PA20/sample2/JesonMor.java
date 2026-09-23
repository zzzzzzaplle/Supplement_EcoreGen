import java.util.ArrayList;

public class JesonMor extends Game {
    public JesonMor() {
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        // Initialize the board from configuration's initialBoard
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
        // Game loop
        while (true) {
            refreshOutput();
            Player current = this.currentPlayer;

            // Get available moves for current player
            Move[] availableMoves = getAvailableMoves(current);

            // Check if player has no available moves
            if (availableMoves == null || availableMoves.length == 0) {
                // Game ends, winner by score comparison
                Player[] players = this.configuration.getPlayers();
                Player other = (players[0] == current) ? players[1] : players[0];
                if (current.getScore() > other.getScore()) {
                    return current;
                } else if (other.getScore() > current.getScore()) {
                    return other;
                } else {
                    return current; // scores equal, current player wins
                }
            }

            // Get player's move
            Move move = current.nextMove(this, availableMoves);
            if (move == null) {
                continue;
            }

            // Validate move through all rules
            if (!validateMove(move)) {
                continue;
            }

            // Execute the move
            Piece piece = this.getPiece(move.getSource());
            movePiece(move);
            updateScore(current, piece, move);
            this.numMoves++;

            // Check winner
            Player winner = getWinner(current, piece, move);
            if (winner != null) {
                refreshOutput();
                return winner;
            }

            // Switch player
            if (this.configuration.getPlayers()[0] == current) {
                this.currentPlayer = this.configuration.getPlayers()[1];
            } else {
                this.currentPlayer = this.configuration.getPlayers()[0];
            }
        }
    }

    private boolean validateMove(Move move) {
        ArrayList<Rule> rules = new ArrayList<>();

        // Global rules
        rules.add(new OutOfBoundaryRule());
        rules.add(new VacantRule());
        rules.add(new NilMoveRule());
        rules.add(new OccupiedRule());

        // Protection rule
        rules.add(new FirstNMovesProtectionRule(this.configuration.getNumMovesProtection()));

        // Piece-specific rules
        Piece piece = this.getPiece(move.getSource());
        if (piece instanceof Knight) {
            rules.add(new KnightMoveRule());
            rules.add(new KnightBlockRule());
        } else if (piece instanceof Archer) {
            rules.add(new ArcherMoveRule());
        }

        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // No winner during protection phase
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }

        // Condition 1: A Knight leaves the central square
        if (lastPiece instanceof Knight) {
            Place centralPlace = this.configuration.getCentralPlace();
            if (lastMove.getSource().equals(centralPlace) && !lastMove.getDestination().equals(centralPlace)) {
                return lastPlayer;
            }
        }

        // Condition 2: Only one player's pieces remain
        Player[] players = this.configuration.getPlayers();
        boolean player0HasPieces = false;
        boolean player1HasPieces = false;
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = this.getPiece(x, y);
                if (p != null) {
                    if (p.getPlayer().equals(players[0])) {
                        player0HasPieces = true;
                    } else {
                        player1HasPieces = true;
                    }
                }
            }
        }
        if (!player0HasPieces) {
            return players[1];
        }
        if (!player1HasPieces) {
            return players[0];
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
        Piece piece = this.getPiece(move.getSource());
        this.board[move.getSource().x()][move.getSource().y()] = null;
        this.board[move.getDestination().x()][move.getDestination().y()] = piece;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        ArrayList<Move> allMoves = new ArrayList<>();
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place source = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, source);
                    for (Move move : pieceMoves) {
                        // Apply rules to filter
                        if (isMoveValidForPlayer(player, move)) {
                            allMoves.add(move);
                        }
                    }
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }

    private boolean isMoveValidForPlayer(Player player, Move move) {
        // Check basic rules
        ArrayList<Rule> rules = new ArrayList<>();
        rules.add(new OutOfBoundaryRule());
        rules.add(new VacantRule());
        rules.add(new NilMoveRule());
        rules.add(new OccupiedRule());
        rules.add(new FirstNMovesProtectionRule(this.configuration.getNumMovesProtection()));

        Piece piece = this.getPiece(move.getSource());
        if (piece instanceof Knight) {
            rules.add(new KnightMoveRule());
            rules.add(new KnightBlockRule());
        } else if (piece instanceof Archer) {
            rules.add(new ArcherMoveRule());
        }

        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        return true;
    }
}

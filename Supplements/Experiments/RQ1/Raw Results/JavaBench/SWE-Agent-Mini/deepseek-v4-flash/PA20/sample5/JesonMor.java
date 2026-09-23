import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {

    public JesonMor() {
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.numMoves = 0;
        // Initialize board from configuration's initialBoard
        int size = configuration.getSize();
        this.board = new Piece[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                this.board[x][y] = configuration.getInitialBoard()[x][y];
            }
        }
        // First player starts
        this.currentPlayer = configuration.getPlayers()[0];
    }

    @Override
    public Player start() {
        while (true) {
            refreshOutput();

            // Get available moves for the current player
            Move[] availableMoves = getAvailableMoves(currentPlayer);

            // Check if current player has no available moves
            if (availableMoves == null || availableMoves.length == 0) {
                // Game ends, winner decided by score comparison
                Player[] players = configuration.getPlayers();
                Player otherPlayer = (players[0].equals(currentPlayer)) ? players[1] : players[0];
                if (currentPlayer.getScore() > otherPlayer.getScore()) {
                    return currentPlayer;
                } else if (otherPlayer.getScore() > currentPlayer.getScore()) {
                    return otherPlayer;
                } else {
                    return currentPlayer; // scores equal, current player wins
                }
            }

            // Ask player for their move
            Move move = currentPlayer.nextMove(this, availableMoves);

            if (move == null) {
                continue;
            }

            // Validate the move against all rules
            if (!isValidMove(move)) {
                System.out.println("Invalid move. Try again.");
                continue;
            }

            // Execute the move
            movePiece(move);

            // Update score
            Piece piece = getPiece(move.getDestination());
            updateScore(currentPlayer, piece, move);

            numMoves++;

            // Check for winner
            Player winner = getWinner(currentPlayer, piece, move);
            if (winner != null) {
                refreshOutput();
                return winner;
            }

            // Switch player
            Player[] players = configuration.getPlayers();
            currentPlayer = (currentPlayer.equals(players[0])) ? players[1] : players[0];
        }
    }

    private boolean isValidMove(Move move) {
        // Get the piece at source
        Piece piece = getPiece(move.getSource());
        if (piece == null) {
            return false;
        }

        // Apply all rules
        List<Rule> rules = getRules();
        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        return true;
    }

    private List<Rule> getRules() {
        List<Rule> rules = new ArrayList<>();
        rules.add(new OutOfBoundaryRule());
        rules.add(new VacantRule());
        rules.add(new NilMoveRule());
        rules.add(new OccupiedRule());
        rules.add(new FirstNMovesProtectionRule(configuration.getNumMovesProtection()));
        rules.add(new KnightMoveRule());
        rules.add(new KnightBlockRule());
        rules.add(new ArcherMoveRule());
        return rules;
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // No winner can be declared within the first numMovesProtection moves
        if (numMoves < configuration.getNumMovesProtection()) {
            return null;
        }

        // Condition 1: A Knight leaves the central square
        Place centralPlace = configuration.getCentralPlace();
        if (lastPiece instanceof Knight &&
            lastMove.getSource().equals(centralPlace) &&
            !lastMove.getDestination().equals(centralPlace)) {
            return lastPlayer;
        }

        // Condition 2: Only one player's pieces remain on the board
        Player[] players = configuration.getPlayers();
        Player otherPlayer = (players[0].equals(lastPlayer)) ? players[1] : players[0];

        boolean otherPlayerPiecesExist = false;
        int size = configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = board[x][y];
                if (piece != null && piece.getPlayer().equals(otherPlayer)) {
                    otherPlayerPiecesExist = true;
                    break;
                }
            }
            if (otherPlayerPiecesExist) break;
        }

        if (!otherPlayerPiecesExist) {
            return lastPlayer;
        }

        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getDestination().getX() - move.getSource().getX()) +
                       Math.abs(move.getDestination().getY() - move.getSource().getY());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        Piece piece = getPiece(source);
        setPiece(destination, piece);
        setPiece(source, null);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> allMoves = new ArrayList<>();
        int size = configuration.getSize();

        // Iterate over all positions on the board
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Place place = new Place(x, y);
                Piece piece = getPiece(place);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Move[] pieceMoves = piece.getAvailableMoves(this, place);
                    if (pieceMoves != null) {
                        for (Move move : pieceMoves) {
                            // Filter by rules
                            Move testMove = new Move(place, move.getDestination());
                            if (isAvailableMove(testMove)) {
                                allMoves.add(testMove);
                            }
                        }
                    }
                }
            }
        }

        return allMoves.toArray(new Move[0]);
    }

    private boolean isAvailableMove(Move move) {
        List<Rule> rules = getRules();
        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        return true;
    }
}

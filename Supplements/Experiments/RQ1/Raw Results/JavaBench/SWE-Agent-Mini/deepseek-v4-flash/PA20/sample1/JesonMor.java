import java.util.ArrayList;
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
        // First player starts
        this.currentPlayer = configuration.getPlayers()[0];
    }

    @Override
    public Player start() {
        while (true) {
            this.refreshOutput();
            
            // Get available moves for current player
            Move[] availableMoves = getAvailableMoves(this.currentPlayer);
            
            // If no available moves, game ends - winner determined by score
            if (availableMoves == null || availableMoves.length == 0) {
                Player[] players = this.configuration.getPlayers();
                Player other = players[0].equals(this.currentPlayer) ? players[1] : players[0];
                
                if (this.currentPlayer.getScore() > other.getScore()) {
                    return this.currentPlayer;
                } else if (other.getScore() > this.currentPlayer.getScore()) {
                    return other;
                } else {
                    return this.currentPlayer;
                }
            }
            
            // Ask player for a move
            Move move = this.currentPlayer.nextMove(this, availableMoves);
            if (move == null) {
                continue;
            }
            
            // Validate the move through rules
            if (!validateMove(move)) {
                continue;
            }
            
            // Execute the move
            movePiece(move);
            
            // Get the piece that was moved
            Piece piece = this.getPiece(move.getDestination());
            
            // Update score
            updateScore(this.currentPlayer, piece, move);
            
            this.numMoves++;
            
            // Check winner
            Player winner = getWinner(this.currentPlayer, piece, move);
            if (winner != null) {
                this.refreshOutput();
                return winner;
            }
            
            // Switch player
            this.currentPlayer = this.currentPlayer.equals(this.configuration.getPlayers()[0]) 
                ? this.configuration.getPlayers()[1] 
                : this.configuration.getPlayers()[0];
        }
    }

    private boolean validateMove(Move move) {
        List<Rule> rules = new ArrayList<>();
        rules.add(new OutOfBoundaryRule());
        rules.add(new VacantRule());
        rules.add(new NilMoveRule());
        rules.add(new OccupiedRule());
        rules.add(new FirstNMovesProtectionRule(this.configuration.getNumMovesProtection()));
        rules.add(new KnightMoveRule());
        rules.add(new KnightBlockRule());
        rules.add(new ArcherMoveRule());
        
        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                System.out.println("Invalid move: " + rule.getDescription());
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
        Place centralPlace = this.configuration.getCentralPlace();
        if (lastPiece instanceof Knight && 
            lastMove.getSource().equals(centralPlace) && 
            !lastMove.getDestination().equals(centralPlace)) {
            return lastPlayer;
        }
        
        // Condition 2: Only one player's pieces remain
        Player otherPlayer = this.configuration.getPlayers()[0].equals(lastPlayer) 
            ? this.configuration.getPlayers()[1] 
            : this.configuration.getPlayers()[0];
        
        boolean otherPlayerHasPieces = false;
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece p = this.getPiece(x, y);
                if (p != null && p.getPlayer().equals(otherPlayer)) {
                    otherPlayerHasPieces = true;
                    break;
                }
            }
            if (otherPlayerHasPieces) break;
        }
        
        if (!otherPlayerHasPieces) {
            return lastPlayer;
        }
        
        return null;
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());
        int distance = dx + dy;
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = this.getPiece(move.getSource());
        // Set destination to the piece
        this.setPiece(move.getDestination(), piece);
        // Clear source
        this.setPiece(move.getSource(), null);
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> allMoves = new ArrayList<>();
        int size = this.configuration.getSize();
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Move[] pieceMoves = piece.getAvailableMoves(this, new Place(x, y));
                    if (pieceMoves != null) {
                        for (Move move : pieceMoves) {
                            // Validate with all rules
                            List<Rule> rules = new ArrayList<>();
                            rules.add(new OutOfBoundaryRule());
                            rules.add(new VacantRule());
                            rules.add(new NilMoveRule());
                            rules.add(new OccupiedRule());
                            rules.add(new FirstNMovesProtectionRule(this.configuration.getNumMovesProtection()));
                            rules.add(new KnightMoveRule());
                            rules.add(new KnightBlockRule());
                            rules.add(new ArcherMoveRule());
                            
                            boolean valid = true;
                            for (Rule rule : rules) {
                                if (!rule.validate(this, move)) {
                                    valid = false;
                                    break;
                                }
                            }
                            if (valid) {
                                allMoves.add(move);
                            }
                        }
                    }
                }
            }
        }
        
        return allMoves.toArray(new Move[0]);
    }
}

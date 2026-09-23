import java.util.*;

public class JesonMor extends Game implements Cloneable {
    public Player start() {
        JesonMor game = this.clone();
        
        // Initialize board from configuration
        Piece[][] initialBoard = game.configuration.getInitialBoard();
        for (int x = 0; x < game.configuration.getSize(); x++) {
            for (int y = 0; y < game.configuration.getSize(); y++) {
                game.board[x][y] = initialBoard[x][y];
            }
        }
        
        game.numMoves = 0;
        game.currentPlayer = game.configuration.getPlayers()[0];
        
        game.refreshOutput();
        
        Player winner = null;
        
        while (winner == null) {
            Move[] availableMoves = game.getAvailableMoves(game.currentPlayer);
            
            if (availableMoves.length == 0) {
                // Current player has no moves, game ends
                Player player1 = game.configuration.getPlayers()[0];
                Player player2 = game.configuration.getPlayers()[1];
                if (player1.getScore() > player2.getScore()) {
                    winner = player1;
                } else if (player2.getScore() > player1.getScore()) {
                    winner = player2;
                } else {
                    // Scores are equal, current player wins
                    winner = game.currentPlayer;
                }
                break;
            }
            
            Move move = game.currentPlayer.nextMove(game, availableMoves);
            
            game.movePiece(move);
            game.updateScore(game.currentPlayer, game.getPiece(move.getDestination()), move);
            
            winner = game.getWinner(game.currentPlayer, game.getPiece(move.getDestination()), move);
            
            // Switch player
            Player[] players = game.configuration.getPlayers();
            if (game.currentPlayer.equals(players[0])) {
                game.currentPlayer = players[1];
            } else {
                game.currentPlayer = players[0];
            }
            
            game.numMoves++;
            game.refreshOutput();
        }
        
        return winner;
    }

    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // No winner can be declared within the first numMovesProtection moves
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            return null;
        }
        
        // Check if Knight left central place
        if (lastPiece instanceof Knight) {
            Place centralPlace = this.getCentralPlace();
            if (lastMove.getSource().equals(centralPlace) && !lastMove.getDestination().equals(centralPlace)) {
                return lastPlayer;
            }
        }
        
        // Check if only one player's pieces remain
        Player player1 = this.configuration.getPlayers()[0];
        Player player2 = this.configuration.getPlayers()[1];
        
        boolean player1HasPieces = false;
        boolean player2HasPieces = false;
        
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.getPiece(x, y);
                if (piece != null) {
                    if (piece.getPlayer().equals(player1)) {
                        player1HasPieces = true;
                    } else if (piece.getPlayer().equals(player2)) {
                        player2HasPieces = true;
                    }
                }
            }
        }
        
        if (!player1HasPieces) {
            return player2;
        } else if (!player2HasPieces) {
            return player1;
        }
        
        return null;
    }

    public void updateScore(Player player, Piece piece, Move move) {
        Place source = move.getSource();
        Place dest = move.getDestination();
        int manhattanDistance = Math.abs(dest.x() - source.x()) + Math.abs(dest.y() - source.y());
        player.setScore(player.getScore() + manhattanDistance);
    }

    public void movePiece(Move move) {
        Place source = move.getSource();
        Place dest = move.getDestination();
        
        Piece piece = this.getPiece(source);
        this.setPiece(source, null);
        this.setPiece(dest, piece);
    }

    public Move[] getAvailableMoves(Player player) {
        List<Move> allMoves = new ArrayList<Move>();
        
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place place = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, place);
                    for (Move m : pieceMoves) {
                        allMoves.add(m);
                    }
                }
            }
        }
        
        return allMoves.toArray(new Move[0]);
    }

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
    }

    public JesonMor() {
    }

    @Override
    public boolean validateMove(Move move, Piece sourcePiece) {
        Rule[] globalRules = {
            new VacantRule(),
            new OccupiedRule(),
            new OutOfBoundaryRule(),
            new NilMoveRule()
        };
        
        for (Rule rule : globalRules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        
        // Apply protection rule
        FirstNMovesProtectionRule protectionRule = new FirstNMovesProtectionRule(this.configuration.getNumMovesProtection());
        if (!protectionRule.validate(this, move)) {
            return false;
        }
        
        // Apply piece-specific rules
        if (sourcePiece instanceof Knight) {
            KnightMoveRule knightMoveRule = new KnightMoveRule();
            KnightBlockRule knightBlockRule = new KnightBlockRule();
            
            if (!knightMoveRule.validate(this, move)) {
                return false;
            }
            if (!knightBlockRule.validate(this, move)) {
                return false;
            }
        } else if (sourcePiece instanceof Archer) {
            ArcherMoveRule archerMoveRule = new ArcherMoveRule();
            if (!archerMoveRule.validate(this, move)) {
                return false;
            }
        }
        
        return true;
    }
    
    public JesonMor clone() {
        try {
            JesonMor cloned = (JesonMor) super.clone();
            cloned.configuration = this.configuration.clone();
            cloned.board = new Piece[cloned.configuration.getSize()][];
            for (int i = 0; i < cloned.configuration.getSize(); i++) {
                cloned.board[i] = new Piece[cloned.configuration.getSize()];
                System.arraycopy(this.board[i], 0, cloned.board[i], 0, cloned.configuration.getSize());
            }
            cloned.currentPlayer = this.currentPlayer == null ? null : this.currentPlayer.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
}

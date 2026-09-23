import java.util.ArrayList;
import java.util.List;

public class JesonMor extends Game {
    
    public JesonMor() {
    }
    
    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.numMoves = 0;
        
        int size = configuration.getSize();
        this.board = new Piece[size][];
        for (int x = 0; x < size; x++) {
            this.board[x] = new Piece[size];
            for (int y = 0; y < size; y++) {
                this.board[x][y] = configuration.getInitialBoard()[x][y];
            }
        }
        
        this.currentPlayer = configuration.getPlayers()[0];
    }
    
    public Player start() {
        Player winner = null;
        
        while (winner == null) {
            refreshOutput();
            
            Move[] availableMoves = getAvailableMoves(currentPlayer);
            
            if (availableMoves.length == 0) {
                Player opponent = configuration.getPlayers()[0].equals(currentPlayer) 
                        ? configuration.getPlayers()[1] 
                        : configuration.getPlayers()[0];
                Move[] opponentMoves = getAvailableMoves(opponent);
                
                if (opponentMoves.length == 0) {
                    if (currentPlayer.getScore() > opponent.getScore()) {
                        winner = currentPlayer;
                    } else if (opponent.getScore() > currentPlayer.getScore()) {
                        winner = opponent;
                    } else {
                        winner = currentPlayer;
                    }
                    break;
                } else {
                    winner = opponent;
                    break;
                }
            }
            
            Move selectedMove = currentPlayer.nextMove(this, availableMoves);
            if (selectedMove == null) {
                continue;
            }
            
            try {
                movePiece(selectedMove);
            } catch (Exception e) {
                refreshOutput();
                continue;
            }
            
            Piece movedPiece = this.getPiece(selectedMove.getDestination());
            updateScore(currentPlayer, movedPiece, selectedMove);
            
            winner = getWinner(currentPlayer, movedPiece, selectedMove);
            
            this.currentPlayer = configuration.getPlayers()[0].equals(currentPlayer) 
                    ? configuration.getPlayers()[1] 
                    : configuration.getPlayers()[0];
        }
        
        return winner;
    }
    
    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        if (this.numMoves <= configuration.getNumMovesProtection()) {
            return null;
        }
        
        if (lastPiece instanceof Knight) {
            if (lastMove.getSource().equals(configuration.getCentralPlace()) && 
                !lastMove.getDestination().equals(configuration.getCentralPlace())) {
                return lastPlayer;
            }
        }
        
        int[] pieceCounts = new int[configuration.getPlayers().length];
        int size = configuration.getSize();
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.getPiece(x, y);
                if (piece != null) {
                    Player player = piece.getPlayer();
                    for (int i = 0; i < configuration.getPlayers().length; i++) {
                        if (player.equals(configuration.getPlayers()[i])) {
                            pieceCounts[i]++;
                            break;
                        }
                    }
                }
            }
        }
        
        int playersWithPieces = 0;
        for (int count : pieceCounts) {
            if (count > 0) {
                playersWithPieces++;
            }
        }
        
        if (playersWithPieces <= 1) {
            for (int i = 0; i < configuration.getPlayers().length; i++) {
                if (pieceCounts[i] > 0) {
                    return configuration.getPlayers()[i];
                }
            }
        }
        
        return null;
    }
    
    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getSource().x() - move.getDestination().x()) +
                       Math.abs(move.getSource().y() - move.getDestination().y());
        
        player.setScore(player.getScore() + distance);
        this.numMoves++;
    }
    
    @Override
    public void movePiece(Move move) {
        Piece piece = getPiece(move.getSource());
        
        if (piece == null) {
            throw new RuntimeException("Source is empty, cannot move piece");
        }
        
        if (!new VacantRule().validate(this, move)) {
            throw new RuntimeException(VacantRule.class.getName() + " failed: " + new VacantRule().getDescription());
        }
        if (!new NilMoveRule().validate(this, move)) {
            throw new RuntimeException(NilMoveRule.class.getName() + " failed: " + new NilMoveRule().getDescription());
        }
        if (!new OutOfBoundaryRule().validate(this, move)) {
            throw new RuntimeException(OutOfBoundaryRule.class.getName() + " failed: " + new OutOfBoundaryRule().getDescription());
        }
        if (!new OccupiedRule().validate(this, move)) {
            throw new RuntimeException(OccupiedRule.class.getName() + " failed: " + new OccupiedRule().getDescription());
        }
        if (!new FirstNMovesProtectionRule(configuration.getNumMovesProtection()).validate(this, move)) {
            throw new RuntimeException(FirstNMovesProtectionRule.class.getName() + " failed: " + new FirstNMovesProtectionRule(configuration.getNumMovesProtection()).getDescription());
        }
        
        if (piece instanceof Knight) {
            if (!new KnightMoveRule().validate(this, move)) {
                throw new RuntimeException(KnightMoveRule.class.getName() + " failed: " + new KnightMoveRule().getDescription());
            }
            if (!new KnightBlockRule().validate(this, move)) {
                throw new RuntimeException(KnightBlockRule.class.getName() + " failed: " + new KnightBlockRule().getDescription());
            }
        } else if (piece instanceof Archer) {
            if (!new ArcherMoveRule().validate(this, move)) {
                throw new RuntimeException(ArcherMoveRule.class.getName() + " failed: " + new ArcherMoveRule().getDescription());
            }
        }
        
        this.setPiece(move.getDestination(), piece);
        this.setPiece(move.getSource(), null);
    }
    
    @Override
    public Move[] getAvailableMoves(Player player) {
        List<Move> moves = new ArrayList<>();
        int size = configuration.getSize();
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.getPiece(x, y);
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place source = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, source);
                    for (Move move : pieceMoves) {
                        moves.add(move);
                    }
                }
            }
        }
        
        return moves.toArray(new Move[0]);
    }
}

public class Archer extends Piece {
    
    public Archer() {
        super(null);
    }
    
    public Archer(Player player) {
        super(player);
    }
    
    public char getLabel() {
        return 'A';
    }
    
    public Move[] getAvailableMoves(Game game, Place source) {
        java.util.List<Move> moves = new java.util.ArrayList<>();
        
        int size = game.getConfiguration().getSize();
        Piece movingPiece = game.getPiece(source);
        Player movingPlayer = movingPiece.getPlayer();
        
        // Four directions: up, down, left, right
        int[] dx = {0, 0, -1, 1};
        int[] dy = {1, -1, 0, 0};
        
        for (int dir = 0; dir < 4; dir++) {
            for (int dist = 1; dist < size; dist++) {
                int destX = source.x() + dx[dir] * dist;
                int destY = source.y() + dy[dir] * dist;
                
                if (destX < 0 || destX >= size || destY < 0 || destY >= size) {
                    break; // Out of boundary, stop in this direction
                }
                
                Place dest = new Place(destX, destY);
                Piece destPiece = game.getPiece(dest);
                
                if (destPiece == null) {
                    // Vacant destination - sliding move
                    Move candidate = new Move(source, dest);
                    
                    // Check if path is clear (no pieces between source and dest)
                    boolean pathClear = true;
                    for (int step = 1; step < dist; step++) {
                        int checkX = source.x() + dx[dir] * step;
                        int checkY = source.y() + dy[dir] * step;
                        if (game.getPiece(checkX, checkY) != null) {
                            pathClear = false;
                            break;
                        }
                    }
                    
                    if (pathClear) {
                        if (new VacantRule().validate(game, candidate) &&
                            new NilMoveRule().validate(game, candidate) &&
                            new OutOfBoundaryRule().validate(game, candidate) &&
                            new OccupiedRule().validate(game, candidate) &&
                            new ArcherMoveRule().validate(game, candidate)) {
                            moves.add(candidate);
                        }
                    }
                } else if (destPiece.getPlayer() != movingPlayer) {
                    // Occupied by opponent - check for capture (exactly one piece between)
                    int screenCount = 0;
                    for (int step = 1; step < dist; step++) {
                        int checkX = source.x() + dx[dir] * step;
                        int checkY = source.y() + dy[dir] * step;
                        if (game.getPiece(checkX, checkY) != null) {
                            screenCount++;
                        }
                    }
                    
                    // For capture, there must be exactly one piece between source and destination
                    if (screenCount == 1) {
                        Move candidate = new Move(source, dest);
                        if (new VacantRule().validate(game, candidate) &&
                            new NilMoveRule().validate(game, candidate) &&
                            new OutOfBoundaryRule().validate(game, candidate) &&
                            new OccupiedRule().validate(game, candidate) &&
                            new ArcherMoveRule().validate(game, candidate)) {
                            moves.add(candidate);
                        }
                    }
                    // If more than one piece, cannot jump over all of them
                    break; // Cannot go further in this direction
                } else {
                    // Blocked by own piece
                    break;
                }
            }
        }
        
        return moves.toArray(new Move[0]);
    }
}

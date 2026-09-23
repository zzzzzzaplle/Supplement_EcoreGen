public class Archer extends Piece {
    public Archer() {
        super();
    }

    public Archer(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'A';
    }

    public Move[] getAvailableMoves(Game game, Place source) {
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};
        
        java.util.List<Move> candidates = new java.util.ArrayList<>();
        int size = game.getConfiguration().getSize();
        
        for (int d = 0; d < 4; d++) {
            for (int step = 1; step < size; step++) {
                int nx = source.x() + dx[d] * step;
                int ny = source.y() + dy[d] * step;
                
                if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                    break;
                }
                
                Piece piece = game.getPiece(nx, ny);
                if (piece == null) {
                    // Vacant square - can move here
                    candidates.add(new Move(source, new Place(nx, ny)));
                } else {
                    // Occupied square
                    // Only capture if we haven't seen a screen yet
                    boolean foundScreen = false;
                    // Look beyond this piece
                    for (int beyond = step + 1; beyond < size; beyond++) {
                        int bx = source.x() + dx[d] * beyond;
                        int by = source.y() + dy[d] * beyond;
                        
                        if (bx < 0 || bx >= size || by < 0 || by >= size) {
                            break;
                        }
                        
                        Piece beyondPiece = game.getPiece(bx, by);
                        if (beyondPiece == null) {
                            // Empty after screen - cannot capture past empty square
                            break;
                        } else {
                            // Found the beyond piece - this is the potential capture target
                            // piece at (nx, ny) is the screen
                            if (!beyondPiece.getPlayer().equals(this.getPlayer())) {
                                candidates.add(new Move(source, new Place(bx, by)));
                            }
                            // After finding a capture target or friendly piece, stop this direction
                            break;
                        }
                    }
                    // Can't move through or to occupied square (unless it's a capture beyond)
                    break;
                }
            }
        }
        
        return candidates.toArray(new Move[0]);
    }
}

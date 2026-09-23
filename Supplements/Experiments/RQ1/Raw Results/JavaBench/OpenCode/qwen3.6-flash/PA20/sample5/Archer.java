public class Archer extends Piece {
    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        java.util.ArrayList<Move> allMoves = new java.util.ArrayList<>();
        int size = game.configurationGetSize();
        int sourceX = source.x();
        int sourceY = source.y();

        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        for (int d = 0; d < 4; d++) {
            for (int dist = 1; dist < size; dist++) {
                int nx = sourceX + dx[d] * dist;
                int ny = sourceY + dy[d] * dist;
                if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                    break;
                }
                Place dest = new Place(nx, ny);
                Piece destPiece = game.getPiece(dest);

                if (destPiece != null) {
                    boolean hasScreen = false;
                    for (int step = 1; step < dist; step++) {
                        int sx = sourceX + dx[d] * step;
                        int sy = sourceY + dy[d] * step;
                        if (game.getPiece(sx, sy) != null) {
                            hasScreen = true;
                            break;
                        }
                    }
                    if (hasScreen) {
                        Move mv = new Move(source, dest);
                        allMoves.add(mv);
                    }
                    break;
                } else {
                    Move mv = new Move(source, dest);
                    allMoves.add(mv);
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }
}

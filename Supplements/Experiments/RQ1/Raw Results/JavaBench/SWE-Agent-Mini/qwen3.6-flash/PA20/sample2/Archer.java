import java.util.*;

public class Archer extends Piece implements Cloneable {
    public char getLabel() {
        return 'A';
    }

    public Move[] getAvailableMoves(Game game, Place source) {
        int directions[][] = {
                {0, 1}, {0, -1},
                {1, 0}, {-1, 0}
        };

        List<Move> availableMoves = new ArrayList<Move>();
        Piece sourcePiece = game.getPiece(source);

        for (int[] direction : directions) {
            int stepX = direction[0];
            int stepY = direction[1];

            int currentX = source.x() + stepX;
            int currentY = source.y() + stepY;

            boolean blocked = false;
            Piece firstPieceOnPath = null;
            boolean afterBlockingPiece = false;

            while (currentX >= 0 && currentX < game.configuration.getSize() &&
                    currentY >= 0 && currentY < game.configuration.getSize()) {
                
                Place currentPlace = new Place(currentX, currentY);
                Piece currentPiece = game.getPiece(currentPlace);

                if (currentPiece == null) {
                    if (!afterBlockingPiece) {
                        Move move = new Move(source, currentPlace);
                        if (game.validateMove(move, this)) {
                            availableMoves.add(move);
                        }
                    } else {
                        break;
                    }
                } else {
                    if (!blocked) {
                        blocked = true;
                        firstPieceOnPath = currentPiece;
                        afterBlockingPiece = true;
                    } else {
                        break;
                    }
                }

                if (afterBlockingPiece && firstPieceOnPath != null) {
                    if (currentPiece != null && !currentPiece.getPlayer().equals(sourcePiece.getPlayer())) {
                        Move move = new Move(source, currentPlace);
                        if (game.validateMove(move, this)) {
                            availableMoves.add(move);
                        }
                    }
                    if (currentPiece.getPlayer().equals(sourcePiece.getPlayer())) {
                        break;
                    }
                }

                currentX += stepX;
                currentY += stepY;
            }
        }
        return availableMoves.toArray(new Move[0]);
    }
    
    public Archer() {
    }
}

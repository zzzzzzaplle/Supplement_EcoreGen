import java.util.ArrayList;
import java.lang.CloneNotSupportedException;

public class KnightMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        Knight piece = (Knight) game.getPiece(move.getSource());
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();
        if (!(Math.abs(dx) == 2 && Math.abs(dy) == 1) &&
            !(Math.abs(dx) == 1 && Math.abs(dy) == 2)) {
            return false;
        }
        return true;
    }

public String getDescription() {
        return "knight move rule is violated";
    }
}

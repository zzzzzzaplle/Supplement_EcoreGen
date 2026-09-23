import java.util.Objects;

public class KnightMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int dx = Math.abs(move.getSource().x() - move.getDestination().x());
        int dy = Math.abs(move.getSource().y() - move.getDestination().y());

        // Knight moves in L shape: (2,1) or (1,2)
        if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
            return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}

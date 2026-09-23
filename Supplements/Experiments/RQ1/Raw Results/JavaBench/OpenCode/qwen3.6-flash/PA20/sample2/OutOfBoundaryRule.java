import java.util.Objects;
import java.util.StringJoiner;

public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (move.getSource().x() < 0 || move.getSource().y() < 0 ||
            move.getDestination().x() < 0 || move.getDestination().y() < 0) {
            return false;
        }
        int size = game.getConfiguration().getSize();
        if (move.getSource().x() >= size || move.getSource().y() >= size ||
            move.getDestination().x() >= size || move.getDestination().y() >= size) {
            return false;
        }
        return true;
    }

public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}

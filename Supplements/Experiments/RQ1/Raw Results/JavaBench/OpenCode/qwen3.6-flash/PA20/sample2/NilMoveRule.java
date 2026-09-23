import java.util.Objects;
import java.util.StringJoiner;

public class NilMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        if (source.equals(destination)) {
            return false;
        }
        return true;
    }

 public String getDescription() {
        return "the source and destination of move should be different places";
    }
}

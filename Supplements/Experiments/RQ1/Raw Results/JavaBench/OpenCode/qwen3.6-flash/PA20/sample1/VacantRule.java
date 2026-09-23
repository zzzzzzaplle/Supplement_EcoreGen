import java.util.Objects;

public class VacantRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        return piece != null;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}

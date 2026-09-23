import edu.pa20.Game;
import edu.pa20.Move;
import edu.pa20.Pa20Factory;
import edu.pa20.Piece;
import edu.pa20.Place;
import edu.pa20.Player;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import java.util.ArrayList;
import java.util.List;

public class MockPiece extends Piece {
    public MockPiece(Player player) {
        setPlayer(player);
    }

    public MockPiece() {
        setPlayer(new MockPlayer());
    }

    @Override
    public char getLabel() {
        return 'M';
    }

    @Override
    public EList<Move> getAvailableMoves(Game game, Place source) {
        List<Move> candidates = new ArrayList<>();
        candidates.add(createMove(source, source.getX() + 1, source.getY()));
        candidates.add(createMove(source, source.getX() - 1, source.getY()));
        candidates.add(createMove(source, source.getX(), source.getY() + 1));
        candidates.add(createMove(source, source.getX(), source.getY() - 1));

        EList<Move> valid = new BasicEList<>();
        for (Move move : candidates) {
            if (validateMove(game, move)) {
                valid.add(move);
            }
        }
        return valid;
    }

    private Move createMove(Place source, int x, int y) {
        var destination = Pa20Factory.eINSTANCE.createPlace();
        destination.setX(x);
        destination.setY(y);

        var move = Pa20Factory.eINSTANCE.createMove();
        move.setSource(source);
        move.setDestination(destination);
        return move;
    }

    private boolean validateMove(Game game, Move move) {
        return move.getDestination().getX() < game.getConfiguration().getSize()
                && move.getDestination().getY() < game.getConfiguration().getSize()
                && move.getSource().getX() < game.getConfiguration().getSize()
                && move.getSource().getY() < game.getConfiguration().getSize()
                && move.getDestination().getX() >= 0
                && move.getDestination().getY() >= 0
                && move.getSource().getX() >= 0
                && move.getSource().getY() >= 0;
    }
}

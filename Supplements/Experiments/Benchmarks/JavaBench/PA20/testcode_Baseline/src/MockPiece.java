
import java.util.ArrayList;
import java.util.Arrays;

public class MockPiece extends Piece {

    private static Place createPlace(int x, int y) {
        Place p = new Place();
        p.setX(x);
        p.setY(y);
        return p;
    }

    private static Move createMove(int sourceX, int sourceY, int destX, int destY) {
        Move m = new Move();
        m.setSource(createPlace(sourceX, sourceY));
        m.setDestination(createPlace(destX, destY));
        return m;
    }

    private static Move createMoveFromPlace(Place source, int destX, int destY) {
        Move m = new Move();
        m.setSource(source);
        m.setDestination(createPlace(destX, destY));
        return m;
    }

    public MockPiece(Player player) {
        super();
        setPlayer(player);
    }

    public MockPiece() {
        super();
        setPlayer(new MockPlayer());
    }

    @Override
    public char getLabel() {
        return 'M';
    }

    protected ArrayList<Move> computeMoves(Game game, Place source) {
        return new ArrayList<>();
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        var moves = new ArrayList<>(Arrays.asList(
                createMoveFromPlace(source, source.getX() + 1, source.getY()),
                createMoveFromPlace(source, source.getX() - 1, source.getY()),
                createMoveFromPlace(source, source.getX(), source.getY() + 1),
                createMoveFromPlace(source, source.getX(), source.getY() - 1)));
        return moves.stream()
                .filter(move -> validateMove(game, move))
                .toArray(Move[]::new);
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KnightTests {
    private Configuration config;
    private MockPlayer player1;
    private MockPlayer player2;

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

    private static JesonMor createJesonMor(Configuration config) {
        return new JesonMor(config);
    }

    private static Knight createKnight(Player player) {
        Knight knight = new Knight();
        knight.setPlayer(player);
        return knight;
    }

    @BeforeEach
    public void setUpGame() {
        this.player1 = new MockPlayer(Color.PURPLE);
        this.player2 = new MockPlayer(Color.YELLOW);
        this.config = new Configuration(5, new Player[]{player1, player2});
    }

    /**
     * Test if the returned value of {@link Knight#getAvailableMoves(Game, Place)} contains and only contains all
     * valid moves.
     */
    @Test
    @SampleTest
    public void testGetAvailableMovesSimple() {
        var knight1 = createKnight(player1);
        var knight2 = createKnight(player2);
        this.config.addInitialPiece(knight1, 0, 0);
        this.config.addInitialPiece(knight2, 4, 4);
        var game = createJesonMor(this.config);
var moves = knight1.getAvailableMoves(game, createPlace(0, 0));
        var expectedMoves = new Move[]{
                createMove(0, 0, 1, 2),
                createMove(0, 0, 2, 1),
        };

        moves = knight2.getAvailableMoves(game, createPlace(4, 4));
        expectedMoves = new Move[]{
                createMove(4, 4, 3, 2),
                createMove(4, 4, 2, 3),
        };
        Assertions.assertTrue(Compares.areContentsEqual(moves, expectedMoves));

        moves = knight2.getAvailableMoves(game, createPlace(4, 4));
        expectedMoves = new Move[]{
                createMove(4, 4, 3, 2),
                createMove(4, 4, 2, 3),
        };
        assertTrue(Compares.areContentsEqual(moves, expectedMoves));
    }

    /**
     * Test if the returned value of {@link Knight#getAvailableMoves(Game, Place)} contains and only contains all
     * valid moves.
     */
    @Test
    @UnitTest
    public void testGetAvailableMovesOccupied() {
        var knight1 = createKnight(player1);
        var knight2 = createKnight(player2);
        this.config.addInitialPiece(knight1, 1, 1);
        this.config.addInitialPiece(knight2, 4, 4);
        this.config.addInitialPiece(new MockPiece(player1), 3, 2);
        var game = createJesonMor(this.config);
        var moves = knight1.getAvailableMoves(game, createPlace(1, 1));
        var expectedMoves = new Move[]{
                createMove(1, 1, 3, 0),
                createMove(1, 1, 0, 3),
                createMove(1, 1, 2, 3),
        };
        assertTrue(Compares.areContentsEqual(moves, expectedMoves));
    }

    /**
     * Test if the returned value of {@link Knight#getAvailableMoves(Game, Place)} contains and only contains all
     * valid moves.
     */
    @Test
    @UnitTest
    public void testGetAvailableMovesCapture() {
        var knight1 = createKnight(player1);
        var knight2 = createKnight(player2);
        this.config.addInitialPiece(knight1, 1, 1);
        this.config.addInitialPiece(knight2, 2, 3);
        var game = createJesonMor(this.config);
        var moves = knight1.getAvailableMoves(game, createPlace(1, 1));
        var expectedMoves = new Move[]{
                createMove(1, 1, 3, 0),
                createMove(1, 1, 0, 3),
                createMove(1, 1, 2, 3),
                createMove(1, 1, 3, 2),
        };
        assertTrue(Compares.areContentsEqual(moves, expectedMoves));
    }

    /**
     * Test if the returned value of {@link Knight#getAvailableMoves(Game, Place)} contains and only contains all
     * valid moves.
     */
    @Test
    @UnitTest
    public void testGetAvailableMovesBlocked() {
        this.config = new Configuration(9, new Player[]{player1, player2});
        var knight1 = createKnight(player1);
        var knight2 = createKnight(player2);
        this.config.addInitialPiece(knight1, 3, 3);
        this.config.addInitialPiece(knight2, 2, 3);
        var game = createJesonMor(this.config);
        var moves = knight1.getAvailableMoves(game, createPlace(3, 3));
        var expectedMoves = new Move[]{
                createMove(3, 3, 2, 1),
                createMove(3, 3, 2, 5),
                createMove(3, 3, 4, 1),
                createMove(3, 3, 4, 5),
                createMove(3, 3, 5, 2),
                createMove(3, 3, 5, 4),
        };
        assertTrue(Compares.areContentsEqual(moves, expectedMoves));
    }
}

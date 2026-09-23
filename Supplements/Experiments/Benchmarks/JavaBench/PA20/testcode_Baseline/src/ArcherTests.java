import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@OptionalArcherImplementation
public class ArcherTests {
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

    private static Archer createArcher(Player player) {
        Archer archer = new Archer();
        archer.setPlayer(player);
        return archer;
    }

    @BeforeEach
    public void setUpGame() {
        this.player1 = new MockPlayer(Color.PURPLE);
        this.player2 = new MockPlayer(Color.YELLOW);
        this.config = new Configuration(3, new Player[]{player1, player2});
    }

    /**
     * Test if the returned value of {@link Archer#getAvailableMoves(Game, Place)} contains and only contains all
     * valid moves.
     */
    @Test
    public void testGetAvailableMovesSimple() {
        var archer1 = createArcher(player1);
        var archer2 = createArcher(player2);
        this.config.addInitialPiece(archer1, 0, 0);
        this.config.addInitialPiece(archer2, 0, 2);
        var game = createJesonMor(this.config);
var moves = archer1.getAvailableMoves(game, createPlace(0, 0));
        var expectedMoves = new Move[]{
                createMove(0, 0, 0, 1),
                createMove(0, 0, 1, 0),
                createMove(0, 0, 2, 0),
        };
        assertTrue(Compares.areContentsEqual(moves, expectedMoves));

        moves = archer2.getAvailableMoves(game, createPlace(0, 2));
        expectedMoves = new Move[]{
                createMove(0, 2, 0, 1),
                createMove(0, 2, 1, 2),
                createMove(0, 2, 2, 2),
        };
        assertTrue(Compares.areContentsEqual(moves, expectedMoves));
    }

    /**
     * Test if the returned value of {@link Archer#getAvailableMoves(Game, Place)} contains and only contains all
     * valid moves.
     */
    @Test
    public void testGetAvailableMovesComplex() {
        var archer1 = createArcher(player1);
        var archer2 = createArcher(player2);
        var piece = new MockPiece(player2);
        this.config.addInitialPiece(archer1, 0, 0);
        this.config.addInitialPiece(archer2, 0, 2);
        this.config.addInitialPiece(piece, 0, 1);
        var game = createJesonMor(this.config);
var moves = archer1.getAvailableMoves(game, createPlace(0, 0));
        var expectedMoves = new Move[]{
                createMove(0, 0, 0, 2),
                createMove(0, 0, 1, 0),
                createMove(0, 0, 2, 0),
        };
        assertTrue(Compares.areContentsEqual(moves, expectedMoves));

        moves = archer2.getAvailableMoves(game, createPlace(0, 2));
        expectedMoves = new Move[]{
                createMove(0, 2, 0, 0),
                createMove(0, 2, 2, 2),
                createMove(0, 2, 1, 2),
        };
        assertTrue(Compares.areContentsEqual(moves, expectedMoves));
    }

}

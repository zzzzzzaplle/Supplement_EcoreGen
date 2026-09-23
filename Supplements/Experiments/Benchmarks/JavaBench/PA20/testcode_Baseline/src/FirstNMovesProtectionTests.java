import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FirstNMovesProtectionTests {
    private MockPlayer player1;
    private MockPlayer player2;
    private Knight knight1;
    private Knight knight2;

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
        this.knight1 = createKnight(player1);
        this.knight2 = createKnight(player2);
    }


    @Test
    @UnitTest
    public void testNoProtection() {
        var config = new Configuration(3, new Player[]{player1, player2}, 0);
        config.addInitialPiece(knight1, 0, 0);
        config.addInitialPiece(knight2, 2, 1);
        var game = createJesonMor(config);
        assertTrue(Arrays.asList(game.getAvailableMoves(player1)).contains(createMove(0, 0, 2, 1)));
    }

    @Test
    @UnitTest
    public void testWithProtection() {
        var config = new Configuration(3, new Player[]{player1, player2}, 5);
        config.addInitialPiece(knight1, 0, 0);
        config.addInitialPiece(knight2, 2, 1);
        var game = createJesonMor(config);
        assertFalse(Arrays.asList(game.getAvailableMoves(player1)).contains(createMove(0, 0, 2, 1)));
    }

    @Test
    @UnitTest
    public void testWithThenWithoutProtection() {
        var config = new Configuration(3, new Player[]{player1, player2}, 5);
        config.addInitialPiece(knight1, 0, 0);
        config.addInitialPiece(knight2, 2, 1);
        var game = createJesonMor(config);
        game.numMoves = 5;
        assertTrue(Arrays.asList(game.getAvailableMoves(player1)).contains(createMove(0, 0, 2, 1)));
    }
}

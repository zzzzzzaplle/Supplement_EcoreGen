import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class GameMockPlayerIntegratedTests {
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
        this.config = new Configuration(3, new Player[]{player1, player2});
    }

    @AfterEach
    public void clear() {
        this.player1 = null;
        this.player2 = null;
        this.config = null;
    }

    @Test
    @SampleTest
    public void testCentralPlaceWin() {
        var config = new Configuration(5, new Player[]{player1, player2});
        config.addInitialPiece(createKnight(player1), 0, 0);
        config.addInitialPiece(createKnight(player2), 4, 4);
        player1.setNextMoves(new Move[]{
                createMove(0, 0, 2, 1),
                createMove(2, 1, 4, 2),
                createMove(4, 2, 3, 0),
                createMove(3, 0, 2, 2),
                createMove(2, 2, 1, 4),
        });
        player2.setNextMoves(new Move[]{
                createMove(4, 4, 2, 3),
                createMove(2, 3, 4, 4),
                createMove(4, 4, 2, 3),
                createMove(2, 3, 4, 4),
        });
        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            var game = createJesonMor(config);
            var winner = game.start();
            assertEquals(player1, winner);
            assertEquals(9, game.getNumMoves());
            assertEquals(15, player1.getScore());
            assertEquals(12, player2.getScore());
        });
    }

    @Test
    @IntegratedTest
    public void testCaptureAllWinSingle() {
        this.config.addInitialPiece(new MockPiece(player1), 0, 0);
        this.config.addInitialPiece(new MockPiece(player2), 2, 2);

        player1.setNextMoves(new Move[]{
                createMove(0, 0, 0, 1),
                createMove(0, 1, 0, 2),
                createMove(0, 2, 1, 2),
        });
        player2.setNextMoves(new Move[]{
                createMove(2, 2, 1, 2),
                createMove(1, 2, 0, 2),
                createMove(0, 2, 0, 1),
        });

        var game = createJesonMor(this.config);
        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            var winner = game.start();
            assertEquals(player2, winner);
            assertEquals(4, game.getNumMoves());
            assertEquals(2, player1.getScore());
            assertEquals(2, player2.getScore());
        });
    }

    @Test
    @IntegratedTest
    public void testCaptureAllWinMultiple() {
        this.config.addInitialPiece(new MockPiece(player1), 0, 0);
        this.config.addInitialPiece(new MockPiece(player1), 1, 0);
        this.config.addInitialPiece(new MockPiece(player2), 2, 2);

        player1.setNextMoves(new Move[]{
                createMove(0, 0, 0, 1),
                createMove(0, 1, 0, 2),
                createMove(1, 0, 0, 0),
                createMove(0, 0, 0, 1),
        });
        player2.setNextMoves(new Move[]{
                createMove(2, 2, 1, 2),
                createMove(1, 2, 0, 2),
                createMove(0, 2, 0, 1),
                createMove(0, 1, 0, 0),
        });
        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            var game = createJesonMor(this.config);
            var winner = game.start();
            assertEquals(player1, winner);
            assertEquals(7, game.getNumMoves());
            assertEquals(4, player1.getScore());
            assertEquals(3, player2.getScore());
        });
    }

    @Test
    @IntegratedTest
    public void testCaptureAndCentralPlaceWin() {
        var config = new Configuration(5, new Player[]{player1, player2});
        config.addInitialPiece(createKnight(player1), 0, 0);
        config.addInitialPiece(createKnight(player1), 2, 3);
        config.addInitialPiece(createKnight(player2), 4, 4);

        player1.setNextMoves(new Move[]{
                createMove(0, 0, 2, 1),
                createMove(2, 1, 4, 2),
                createMove(4, 2, 3, 0),
                createMove(3, 0, 2, 2),
                createMove(2, 2, 1, 4),
        });
        player2.setNextMoves(new Move[]{
                createMove(4, 4, 2, 3),
                createMove(2, 3, 4, 4),
                createMove(4, 4, 2, 3),
                createMove(2, 3, 4, 4),
        });
        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            var game = createJesonMor(config);
            var winner = game.start();
            assertEquals(player1, winner);
            assertEquals(9, game.getNumMoves());
            assertEquals(15, player1.getScore());
            assertEquals(12, player2.getScore());
        });
    }
}

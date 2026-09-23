import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@IntegratedTest
public class IntegratedTestsWithoutArcherTests {
    private Game game;
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
        var size = 5;
        var numMovesProtection = 0;
        this.player1 = new MockPlayer(Color.GREEN);
        this.player2 = new MockPlayer();
        Configuration configuration = new Configuration(size, new Player[]{player1, player2}, numMovesProtection);
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                configuration.addInitialPiece(createKnight(player2), i, size - 1);
            } else {
                configuration.addInitialPiece(createKnight(player2), i, size - 1);
            }
        }
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                configuration.addInitialPiece(createKnight(player1), i, 0);
            } else {
                configuration.addInitialPiece(createKnight(player1), i, 0);
            }
        }
        this.game = createJesonMor(configuration);
    }

    @Test
    public void testCaptureAllWin() {
        player1.setNextMoves(
                new Move[]{
                        createMove(0, 0, 1, 2),
                        createMove(1, 2, 0, 4),
                        createMove(4, 0, 3, 2),
                        createMove(3, 2, 4, 4),
                        createMove(1, 0, 0, 2),
                        createMove(3, 0, 4, 2)
                }
        );
        player2.setNextMoves(
                new Move[]{
                        createMove(3, 4, 4, 2),
                        createMove(2, 4, 3, 2),
                        createMove(4, 2, 3, 4),
                        createMove(1, 4, 0, 2),
                        createMove(3, 4, 4, 2)
                }
        );

        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            var winner = this.game.start();
            assertEquals(player1, winner);
            assertEquals(18, player1.getScore());
            assertEquals(15, player2.getScore());
            assertEquals(11, game.getNumMoves());
        });
    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    public void testLeaveCentralPlaceWin() {
        player1.setNextMoves(
                new Move[]{
                        createMove(1, 0, 2, 2),
                        createMove(2, 0, 0, 1),
                        createMove(4, 0, 3, 2)
                }
        );
        player2.setNextMoves(
                new Move[]{
                        createMove(2, 4, 3, 2),
                        createMove(1, 4, 2, 2),
                        createMove(2, 2, 0, 3)
                }
        );

        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            var winner = this.game.start();
            assertEquals(player2, winner);
            assertEquals(9, player1.getScore());
            assertEquals(9, player2.getScore());
            assertEquals(6, game.getNumMoves());
        });
    }
}

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

@OptionalArcherImplementation
@IntegratedTest
public class IntegratedTestsWithArcherTests {
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

    private static Archer createArcher(Player player) {
        Archer archer = new Archer();
        archer.setPlayer(player);
        return archer;
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
                configuration.addInitialPiece(createArcher(player2), i, size - 1);
            }
        }
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                configuration.addInitialPiece(createKnight(player1), i, 0);
            } else {
                configuration.addInitialPiece(createArcher(player1), i, 0);
            }
        }
        this.game = createJesonMor(configuration);
    }

    @Test
    public void testCaptureAllWin() {
        player1.setNextMoves(
                new Move[]{
                        createMove(0, 0, 1, 2),
                        createMove(1, 0, 1, 4),
                        createMove(1, 4, 4, 4),
                        createMove(4, 0, 3, 2),
                        createMove(2, 0, 0, 1),
                        createMove(0, 1, 1, 3),
                        createMove(3, 2, 1, 1),
                }
        );
        player2.setNextMoves(
                new Move[]{
                        createMove(2, 4, 3, 2),
                        createMove(3, 4, 2, 4),
                        createMove(2, 4, 2, 1),
                        createMove(2, 1, 0, 1),
                        createMove(0, 4, 2, 3),
                        createMove(2, 3, 1, 1)
                }
        );

        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            var winner = this.game.start();
            assertEquals(player1, winner);
            assertEquals(22, player1.getScore());
            assertEquals(15, player2.getScore());
            assertEquals(13, game.getNumMoves());
        });
    }

    @Test
    @IntegratedTest
    public void testTieBreak() {
        var size = 3;
        var numMovesProtection = 100;
        var player1 = new MockPlayer(Color.GREEN);
        var player2 = new MockPlayer(Color.CYAN);
        Configuration configuration = new Configuration(size, new Player[]{player1, player2}, numMovesProtection);
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                configuration.addInitialPiece(createKnight(player2), i, size - 1);
            } else {
                configuration.addInitialPiece(createArcher(player2), i, size - 1);
            }
        }
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                configuration.addInitialPiece(createKnight(player1), i, 0);
            } else {
                configuration.addInitialPiece(createArcher(player1), i, 0);
            }
        }
        var game = createJesonMor(configuration);
        var data = new String[]{
                "b1->b2\r\n",
                "c3->b1\r\n",
                "b2->a2\r\n",
                "b1->c3\r\n",
                "a2->b2\r\n",
                "a3->b1\r\n",
                "b2->c2\r\n",
                "b1->a3\r\n",
                "c1->a2\r\n",
                "b3->b1\r\n",
                "c2->b2\r\n",
                "b1->c1\r\n",
                "a1->c2\r\n",
                "c1->a1\r\n",
                "b2->b1\r\n"
        };
        player1.setNextMoves(new Move[]{
                createMove(1, 0, 1, 1),
                createMove(1, 1, 0, 1),
                createMove(0, 1, 1, 1),
                createMove(1, 1, 2, 1),
                createMove(2, 0, 0, 1),
                createMove(2, 1, 1, 1),
                createMove(0, 0, 2, 1),
                createMove(1, 1, 1, 0),
        });
        player2.setNextMoves(new Move[]{
                createMove(2, 2, 1, 0),
                createMove(1, 0, 2, 2),
                createMove(0, 2, 1, 0),
                createMove(1, 0, 0, 2),
                createMove(1, 2, 1, 0),
                createMove(1, 0, 2, 0),
                createMove(2, 0, 0, 0),
        });
        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            var winner = game.start();
            assertEquals(player1, winner);
        });
    }
}

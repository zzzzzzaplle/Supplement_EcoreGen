import edu.pa20.*;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;

@OptionalArcherImplementation
@IntegratedTest
public class IntegratedTestsWithArcherTests {
    private Game game;
    private MockPlayer player1;
    private MockPlayer player2;

    @BeforeEach
    public void setUpGame() {
        var size = 5;
        var numMovesProtection = 0;
        this.player1 = new MockPlayer(Color.GREEN);
        this.player2 = new MockPlayer();
        Configuration configuration = createConfiguration(size, new Player[]{player1, player2}, numMovesProtection);
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
        this.game = createGame(configuration);
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
        Configuration configuration = createConfiguration(size, new Player[]{player1, player2}, numMovesProtection);
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
        var game = createGame(configuration);
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
            assertEquals(player2, winner);
        });
    }

    private static Configuration createConfiguration(int size, Player[] players, int numMovesProtection) {
        Configuration configuration = Pa20Factory.eINSTANCE.createConfiguration();
        EList<Player> playerList = new BasicEList<>();
        if (players != null) {
            for (Player player : players) {
                playerList.add(player);
            }
        }
        configuration.InitConfiguration(size, playerList, numMovesProtection);
        return configuration;
    }

    private static JesonMor createGame(Configuration configuration) {
        JesonMor game = Pa20Factory.eINSTANCE.createJesonMor();
        game.setConfiguration(configuration);
        game.setBoard(configuration.getInitialBoard());
        if (!configuration.getPlayers().isEmpty()) {
            game.setCurrentPlayer(configuration.getPlayers().get(0));
        }
        return game;
    }

    private static Knight createKnight(Player player) {
        Knight knight = Pa20Factory.eINSTANCE.createKnight();
        knight.setPlayer(player);
        return knight;
    }

    private static Archer createArcher(Player player) {
        Archer archer = Pa20Factory.eINSTANCE.createArcher();
        archer.setPlayer(player);
        return archer;
    }

    private static Move createMove(int sourceX, int sourceY, int destinationX, int destinationY) {
        Move move = Pa20Factory.eINSTANCE.createMove();
        move.setSource(createPlace(sourceX, sourceY));
        move.setDestination(createPlace(destinationX, destinationY));
        return move;
    }

    private static Place createPlace(int x, int y) {
        Place place = Pa20Factory.eINSTANCE.createPlace();
        place.setX(x);
        place.setY(y);
        return place;
    }
}

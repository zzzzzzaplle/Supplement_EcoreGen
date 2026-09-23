import edu.pa20.Archer;
import edu.pa20.Configuration;
import edu.pa20.JesonMor;
import edu.pa20.Knight;
import edu.pa20.Move;
import edu.pa20.Pa20Factory;
import edu.pa20.Place;
import edu.pa20.Player;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class GameTieBreakerTests {
    @Test
    @OptionalArcherImplementation
    public void testMoveAndDeadlock() {
        var player1 = new MockPlayer();
        var player2 = new MockPlayer();
        var config = createConfiguration(3, new Player[]{player1, player2});
        config.addInitialPiece(createKnight(player1), 0, 0);
        config.addInitialPiece(createArcher(player1), 1, 0);
        config.addInitialPiece(createKnight(player1), 2, 0);
        config.addInitialPiece(createKnight(player2), 0, 2);
        config.addInitialPiece(createArcher(player2), 1, 2);
        config.addInitialPiece(createKnight(player2), 2, 2);
        var game = createGame(config);
        player1.setNextMoves(new Move[]{
            createMove(1, 0, 1, 1),
            createMove(2, 0, 1, 2),
        });
        player2.setNextMoves(new Move[]{
            createMove(0, 2, 1, 0),
        });
        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            var winner = game.start();
            assertEquals(player1, winner);
            assertEquals(4, player1.getScore());
            assertEquals(3, player2.getScore());
            assertEquals(3, game.getNumMoves());
        });
    }

    private static Configuration createConfiguration(int size, Player[] players) {
        Configuration configuration = Pa20Factory.eINSTANCE.createConfiguration();
        EList<Player> playerList = new BasicEList<>();
        if (players != null) {
            for (Player player : players) {
                playerList.add(player);
            }
        }
        configuration.InitConfiguration(size, playerList, 0);
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

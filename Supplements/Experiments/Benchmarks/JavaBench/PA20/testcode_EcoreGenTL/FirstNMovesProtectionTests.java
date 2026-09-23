import edu.pa20.Color;
import edu.pa20.Configuration;
import edu.pa20.Game;
import edu.pa20.JesonMor;
import edu.pa20.Move;
import edu.pa20.Knight;
import edu.pa20.Pa20Factory;
import edu.pa20.Player;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FirstNMovesProtectionTests {
    private MockPlayer player1;
    private MockPlayer player2;
    private Knight knight1;
    private Knight knight2;

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
        var config = createConfiguration(3, new Player[]{player1, player2}, 0);
        config.addInitialPiece(knight1, 0, 0);
        config.addInitialPiece(knight2, 2, 1);
        var game = createGame(config);
        assertTrue(containsMove(game, player1, createMove(0, 0, 2, 1)));
    }

    @Test
    @UnitTest
    public void testWithProtection() {
        var config = createConfiguration(3, new Player[]{player1, player2}, 5);
        config.addInitialPiece(knight1, 0, 0);
        config.addInitialPiece(knight2, 2, 1);
        var game = createGame(config);
        assertFalse(containsMove(game, player1, createMove(0, 0, 2, 1)));
    }

    @Test
    @UnitTest
    public void testWithThenWithoutProtection() {
        var config = createConfiguration(3, new Player[]{player1, player2}, 5);
        config.addInitialPiece(knight1, 0, 0);
        config.addInitialPiece(knight2, 2, 1);
        var game = createGame(config);
        game.setNumMoves(5);
        assertTrue(containsMove(game, player1, createMove(0, 0, 2, 1)));
    }

    private static boolean containsMove(Game game, Player player, Move expected) {
        for (Move move : game.getAvailableMoves(player)) {
            if (move.equal(expected)) {
                return true;
            }
        }
        return false;
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

    private static Move createMove(int sourceX, int sourceY, int destinationX, int destinationY) {
        Move move = Pa20Factory.eINSTANCE.createMove();
        move.setSource(createPlace(sourceX, sourceY));
        move.setDestination(createPlace(destinationX, destinationY));
        return move;
    }

    private static edu.pa20.Place createPlace(int x, int y) {
        edu.pa20.Place place = Pa20Factory.eINSTANCE.createPlace();
        place.setX(x);
        place.setY(y);
        return place;
    }
}

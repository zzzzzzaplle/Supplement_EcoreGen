import edu.pa20.Color;
import edu.pa20.Configuration;
import edu.pa20.JesonMor;
import edu.pa20.Move;
import edu.pa20.Pa20Factory;
import edu.pa20.Place;
import edu.pa20.Player;
import edu.pa20.RandomPlayer;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomPlayerTests {
    private Configuration config;
    private MockPlayer player1;
    private RandomPlayer player2;

    @BeforeEach
    public void setUpGame() {
        this.player1 = new MockPlayer(Color.PURPLE);
        this.player2 = createRandomPlayer("RandomPlayer");
        this.config = createConfiguration(3, new Player[]{player1, player2});
    }

    @Test
    @SampleTest
    public void testNextMove() {
        var piece1 = new MockPiece(player1);
        var piece2 = new MockPiece(player2);
        this.config.addInitialPiece(piece1, 0, 0);
        this.config.addInitialPiece(piece2, 2, 2);
        var game = createGame(this.config);
        var availableMoves = game.getAvailableMoves(player2);
        var move = player2.nextMove(game, availableMoves);
        assertTrue(containsMove(availableMoves, move));
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

    private static RandomPlayer createRandomPlayer(String name) {
        RandomPlayer player = Pa20Factory.eINSTANCE.createRandomPlayer();
        player.setName(name);
        return player;
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

    private static boolean containsMove(EList<Move> moves, Move expected) {
        for (Move move : moves) {
            if (move != null && move.equal(expected)) {
                return true;
            }
        }
        return false;
    }
}

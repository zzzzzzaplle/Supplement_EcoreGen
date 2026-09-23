import edu.pa20.Archer;
import edu.pa20.Color;
import edu.pa20.Configuration;
import edu.pa20.Game;
import edu.pa20.JesonMor;
import edu.pa20.Move;
import edu.pa20.Pa20Factory;
import edu.pa20.Place;
import edu.pa20.Player;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@OptionalArcherImplementation
public class ArcherTests {
    private Configuration config;
    private MockPlayer player1;
    private MockPlayer player2;

    @BeforeEach
    public void setUpGame() {
        this.player1 = new MockPlayer(Color.PURPLE);
        this.player2 = new MockPlayer(Color.YELLOW);
        this.config = createConfiguration(3, player1 , player2);
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
        var game = createGame(this.config);
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
        var game = createGame(this.config);
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

    private static Configuration createConfiguration(int size, Player... players) {
        Configuration configuration = Pa20Factory.eINSTANCE.createConfiguration();
        EList<Player> playerList = new BasicEList<>();
        for (Player player : players) {
            playerList.add(player);
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

    private static Archer createArcher(Player player) {
        Archer archer = Pa20Factory.eINSTANCE.createArcher();
        archer.setPlayer(player);
        return archer;
    }

    private static Place createPlace(int x, int y) {
        Place place = Pa20Factory.eINSTANCE.createPlace();
        place.setX(x);
        place.setY(y);
        return place;
    }

    private static Move createMove(int sourceX, int sourceY, int destinationX, int destinationY) {
        Move move = Pa20Factory.eINSTANCE.createMove();
        move.setSource(createPlace(sourceX, sourceY));
        move.setDestination(createPlace(destinationX, destinationY));
        return move;
    }

}

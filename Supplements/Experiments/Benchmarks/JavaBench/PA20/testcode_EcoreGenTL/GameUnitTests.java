import edu.pa20.Archer;
import edu.pa20.Color;
import edu.pa20.Configuration;
import edu.pa20.Game;
import edu.pa20.JesonMor;
import edu.pa20.Knight;
import edu.pa20.Move;
import edu.pa20.Pa20Factory;
import edu.pa20.Piece;
import edu.pa20.Place;
import edu.pa20.Player;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class GameUnitTests {
    @Test
    @SampleTest
    public void testGetPiece() {
        var player = new MockPlayer();
        var config = createConfiguration(3, new Player[]{player, new MockPlayer()});

        var piece1 = new MockPiece(player);
        config.addInitialPiece(piece1, 0, 0);
        Assertions.assertEquals(piece1, createGame(config).getPiece(0, 0));

        var piece2 = new MockPiece(player);
        config.addInitialPiece(piece2, 0, 0);
        assertNull(createGame(config).getPiece(1, 1));
    }

    @Test
    @SampleTest
    public void testDefaultValues() {
        var player = new MockPlayer();
        var config = createConfiguration(3, new Player[]{player, new MockPlayer()});
        var game = createGame(config);
        assertEquals(0, game.getNumMoves());
        assertEquals(player, game.getCurrentPlayer());
        assertEquals(config, game.getConfiguration());
    }

    /**
     * Test a normal move
     **/
    @Test
    @SampleTest
    public void testMovePieceNormal() {
        var player = new MockPlayer();
        var piece = new MockPiece(player);
        var config = createConfiguration(3, new Player[]{player, new MockPlayer()});
        config.addInitialPiece(piece, 0, 0);
        var game = createGame(config);
        var beforeBoard = game.clone().getBoard();
        game.movePiece(createMove(0, 0, 1, 0));
        assertNull(game.getPiece(0, 0));
        assertEquals(piece, game.getPiece(1, 0));
        Assertions.assertFalse(Compares.isBoardEqual(beforeBoard, game.getBoard()));
    }

    @Test
    @UnitTest
    public void testMovePieceCapture() {
        var player1 = new MockPlayer();
        var player2 = new MockPlayer();
        var piece1 = new MockPiece(player1);
        var piece2 = new MockPiece(player2);
        var config = createConfiguration(3, new Player[]{player1, player2});
        config.addInitialPiece(piece1, 0, 0);
        config.addInitialPiece(piece2, 0, 1);
        var game = createGame(config);
        var beforeBoard = game.clone().getBoard();
        game.movePiece(createMove(0, 0, 0, 1));
        assertNull(game.getPiece(0, 0));
        assertEquals(piece1, game.getPiece(0, 1));
        Assertions.assertFalse(Compares.isBoardEqual(beforeBoard, game.getBoard()));
        for (int i = 0; i < config.getSize(); i++) {
            for (int j = 0; j < config.getSize(); j++) {
                assertNotEquals(piece2, game.getBoard()[i][j]);
            }
        }
    }

    /**
     * The returned available moves should be valid.
     */
    @Test
    @SampleTest
    public void testGetAvailableMovesNormal() {
        var player = new MockPlayer();
        var config = createConfiguration(5, new Player[]{player, new MockPlayer()});
        config.addInitialPiece(new MockPiece(player), 1, 1);
        var game = createGame(config);
        var expectMoves = new Move[]{
            createMove(1, 1, 0, 1),
            createMove(1, 1, 1, 0),
            createMove(1, 1, 1, 2),
            createMove(1, 1, 2, 1),
        };
        assertTrue(Compares.areContentsEqual(game.getAvailableMoves(player), expectMoves));
    }

    /**
     * If the player has no piece on board, {@link Game#getAvailableMoves(Player)} should return empty array
     */
    @Test
    @UnitTest
    public void testGetAvailableMovesNoPiece() {
        var player = new MockPlayer();
        var player1 = new MockPlayer();
        var config = createConfiguration(3, new Player[]{player, player1});
        config.addInitialPiece(new MockPiece(player1), 1, 2);
        var game = createGame(config);
        assertEquals(0, game.getAvailableMoves(player).size());
    }

    /**
     * The score is calculated according to Manhattan distance between source and destination
     */
    @Test
    @SampleTest
    public void testUpdateScoreBasic1() {
        var player = new MockPlayer();
        var config = createConfiguration(3, new Player[]{player, new MockPlayer()});
        var piece = new MockPiece(player);
        config.addInitialPiece(piece, 0, 0);
        var game = createGame(config);
        var move = createMove(0, 0, 1, 0);
        game.movePiece(move);
        game.updateScore(player, piece, move);
        assertEquals(1, player.getScore());
    }

    /**
     * The score is calculated according to Manhattan distance between source and destination
     */
    @Test
    @UnitTest
    public void testUpdateScoreBasic2() {
        var player = new MockPlayer();
        var config = createConfiguration(9, new Player[]{player, new MockPlayer()});
        var piece = new MockPiece(player);
        config.addInitialPiece(piece, 8, 3);
        var game = createGame(config);
        var move = createMove(8, 3, 7, 3);
        game.movePiece(move);
        game.updateScore(player, piece, move);
        assertEquals(1, player.getScore());
    }

    /**
     * The score is calculated according to Manhattan distance between source and destination
     */
    @Test
    @UnitTest
    public void testUpdateScoreZero() {
        var player = new MockPlayer();
        var config = createConfiguration(3, new Player[]{player, new MockPlayer()});
        var piece = new MockPiece(player);
        config.addInitialPiece(piece, 0, 0);
        var game = createGame(config);
        var move = createMove(0, 0, 0, 0);
        try {
            game.movePiece(move);
        } catch (Throwable ignored) {
        }
        game.updateScore(player, piece, move);
        assertEquals(0, player.getScore());
    }

    @Test
    @SampleTest
    public void testWinByLeaveCentralPlace() {
        var player1 = new MockPlayer();
        var player2 = new MockPlayer();
        var piece1 = createKnight(player1);
        var config = createConfiguration(5, new Player[]{player1, player2});
        config.addInitialPiece(piece1, 1, 0);
        config.addInitialPiece(createKnight(player2), 2, 1);
        var game = createGame(config);
        game.getBoard()[1][4] = game.getBoard()[1][0];
        game.getBoard()[1][0] = null;
        game.getBoard()[3][3] = game.getBoard()[2][1];
        game.getBoard()[2][1] = null;
        game.setNumMoves(3);
        game.setCurrentPlayer(player1);
        var winner = game.getWinner(player1, piece1, createMove(2, 2, 1, 4));
        assertEquals(player1, winner);
    }

    @Test
    @UnitTest
    public void testWinByCaptureAll() {
        var player1 = new MockPlayer();
        var player2 = new MockPlayer();
        var piece2 = new MockPiece(player2);
        var config = createConfiguration(5, new Player[]{player1, player2});
        config.addInitialPiece(new MockPiece(player1), 3, 2);
        config.addInitialPiece(piece2, 2, 1);
        var game = createGame(config);
        game.getBoard()[3][2] = null;
        game.setNumMoves(4);
        game.setCurrentPlayer(player2);
        var winner = game.getWinner(player2, piece2, createMove(1, 1, 2, 1));
        assertEquals(player2, winner);
    }

    @Test
    @OptionalArcherImplementation
    public void testNotWinByArcherLeavingCentralPlace() {
        var player1 = new MockPlayer();
        var player2 = new MockPlayer();
        var archer2 = createArcher(player2);
        var config = createConfiguration(5, new Player[]{player1, player2});
        config.addInitialPiece(new MockPiece(player1), 3, 2);
        config.addInitialPiece(archer2, 2, 1);
        var game = createGame(config);
        game.getBoard()[2][3] = game.getBoard()[3][2];
        game.getBoard()[2][2] = archer2;
        game.setNumMoves(4);
        game.setCurrentPlayer(player2);
        var winner = game.getWinner(player2, archer2, createMove(2, 2, 3, 2));
        assertNull(winner);
    }

    @Test
    @UnitTest
    public void testCannotWinWithinProtection() {
        var player1 = new MockPlayer();
        var player2 = new MockPlayer();
        var piece1 = new MockPiece(player1);
        var config = createConfiguration(5, new Player[]{player1, player2}, 5);
        config.addInitialPiece(piece1, 3, 2);
        config.addInitialPiece(new MockPiece(player2), 2, 1);
        var game = createGame(config);
        game.getBoard()[1][2] = game.getBoard()[3][2];
        game.getBoard()[3][2] = null;
        game.setNumMoves(3);
        game.setCurrentPlayer(player1);
        var winner = game.getWinner(player1, piece1, createMove(2, 2, 1, 2));
        assertNull(winner);
    }

    private static Configuration createConfiguration(int size, Player[] players) {
        return createConfiguration(size, players, 0);
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

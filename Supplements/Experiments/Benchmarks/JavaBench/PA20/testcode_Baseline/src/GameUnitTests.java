import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class GameUnitTests {

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

    @Test
    @SampleTest
    public void testGetPiece() {
        var player = new MockPlayer();
        var config = new Configuration(3, new Player[]{player, new MockPlayer()});

        var piece1 = new MockPiece(player);
        config.addInitialPiece(piece1, 0, 0);
        Assertions.assertEquals(piece1, createJesonMor(config).getPiece(0, 0));

        var piece2 = new MockPiece(player);
        config.addInitialPiece(piece2, 0, 0);
        assertNull(createJesonMor(config).getPiece(1, 1));
    }

    @Test
    @SampleTest
    public void testDefaultValues() {
        var player = new MockPlayer();
        var config = new Configuration(3, new Player[]{player, new MockPlayer()});
        var game = createJesonMor(config);
        assertEquals(0, game.getNumMoves());
        assertEquals(player, game.getCurrentPlayer());
        assertEquals(config, game.getConfiguration());
    }

    /**
     * Test a normal move
     **/
    @Test
    @SampleTest
    public void testMovePieceNormal() throws CloneNotSupportedException {
        var player = new MockPlayer();
        var piece = new MockPiece(player);
        var config = new Configuration(3, new Player[]{player, new MockPlayer()});
        config.addInitialPiece(piece, 0, 0);
        var game = createJesonMor(config);
        var beforeBoard = game.clone().board;
        game.movePiece(createMove(0, 0, 1, 0));
        assertNull(game.getPiece(0, 0));
        assertEquals(piece, game.getPiece(1, 0));
        Assertions.assertFalse(Compares.isBoardEqual(beforeBoard, game.board));
    }

    @Test
    @UnitTest
    public void testMovePieceCapture() throws CloneNotSupportedException {
        var player1 = new MockPlayer();
        var player2 = new MockPlayer();
        var piece1 = new MockPiece(player1);
        var piece2 = new MockPiece(player2);
        var config = new Configuration(3, new Player[]{player1, player2});
        config.addInitialPiece(piece1, 0, 0);
        config.addInitialPiece(piece2, 0, 1);
        var game = createJesonMor(config);
        var beforeBoard = game.clone().board;
        game.movePiece(createMove(0, 0, 0, 1));
        assertNull(game.getPiece(0, 0));
        assertEquals(piece1, game.getPiece(0, 1));
        Assertions.assertFalse(Compares.isBoardEqual(beforeBoard, game.board));
        for (int i = 0; i < config.getSize(); i++) {
            for (int j = 0; j < config.getSize(); j++) {
                assertNotEquals(piece2, game.board[i][j]);
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
        var config = new Configuration(5, new Player[]{player, new MockPlayer()});
        config.addInitialPiece(new MockPiece(player), 1, 1);
        var game = createJesonMor(config);
        var expectMoves = new Move[]{
                createMove(1, 1, 0, 1),
                createMove(1, 1, 1, 0),
                createMove(1, 1, 1, 2),
                createMove(1, 1, 2, 1),
        };
        assertTrue(Compares.areContentsEqual(expectMoves, game.getAvailableMoves(player)));
    }

    /**
     * If the player has no piece on board, {@link Game#getAvailableMoves(Player)} should return empty array
     */
    @Test
    @UnitTest
    public void testGetAvailableMovesNoPiece() {
        var player = new MockPlayer();
        var player1 = new MockPlayer();
        var config = new Configuration(3, new Player[]{player, player1});
        config.addInitialPiece(new MockPiece(player1), 1, 2);
        var game = createJesonMor(config);
        assertEquals(0, game.getAvailableMoves(player).length);
    }

    /**
     * The score is calculated according to Manhattan distance between source and destination
     */
    @Test
    @SampleTest
    public void testUpdateScoreBasic1() {
        var player = new MockPlayer();
        var config = new Configuration(3, new Player[]{player, new MockPlayer()});
        var piece = new MockPiece(player);
        config.addInitialPiece(piece, 0, 0);
        var game = createJesonMor(config);
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
        var config = new Configuration(9, new Player[]{player, new MockPlayer()});
        var piece = new MockPiece(player);
        config.addInitialPiece(piece, 8, 3);
        var game = createJesonMor(config);
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
        var config = new Configuration(3, new Player[]{player, new MockPlayer()});
        var piece = new MockPiece(player);
        config.addInitialPiece(piece, 0, 0);
        var game = createJesonMor(config);
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
        var config = new Configuration(5, new Player[]{player1, player2});
        config.addInitialPiece(piece1, 1, 0);
        config.addInitialPiece(createKnight(player2), 2, 1);
        var game = createJesonMor(config);
        game.board[1][4] = game.board[1][0];
        game.board[1][0] = null;
        game.board[3][3] = game.board[2][1];
        game.board[2][1] = null;
        game.numMoves = 3;
        game.currentPlayer = player1;
        var winner = game.getWinner(player1, piece1, createMove(2, 2, 1, 4));
        assertEquals(player1, winner);
    }

    @Test
    @UnitTest
    public void testWinByCaptureAll() {
        var player1 = new MockPlayer();
        var player2 = new MockPlayer();
        var piece2 = new MockPiece(player2);
        var config = new Configuration(5, new Player[]{player1, player2});
        config.addInitialPiece(new MockPiece(player1), 3, 2);
        config.addInitialPiece(piece2, 2, 1);
        var game = createJesonMor(config);
        game.board[3][2] = null;
        game.numMoves = 4;
        game.currentPlayer = player2;
        var winner = game.getWinner(player2, piece2, createMove(1, 1, 2, 1));
        assertEquals(player2, winner);
    }

    @Test
    @OptionalArcherImplementation
    public void testNotWinByArcherLeavingCentralPlace() {
        var player1 = new MockPlayer();
        var player2 = new MockPlayer();
        var archer2 = createArcher(player2);
        var config = new Configuration(5, new Player[]{player1, player2});
        config.addInitialPiece(new MockPiece(player1), 3, 2);
        config.addInitialPiece(archer2, 2, 1);
        var game = createJesonMor(config);
        game.board[2][3] = game.board[3][2];
        game.board[2][2] = archer2;
        game.numMoves = 4;
        game.currentPlayer = player2;
        var winner = game.getWinner(player2, archer2, createMove(2, 2, 3, 2));
        assertNull(winner);
    }

    @Test
    @UnitTest
    public void testCannotWinWithinProtection() {
        var player1 = new MockPlayer();
        var player2 = new MockPlayer();
        var piece1 = new MockPiece(player1);
        var config = new Configuration(5, new Player[]{player1, player2}, 5);
        config.addInitialPiece(piece1, 3, 2);
        config.addInitialPiece(new MockPiece(player2), 2, 1);
        var game = createJesonMor(config);
        game.board[1][2] = game.board[3][2];
        game.board[3][2] = null;
        game.numMoves = 3;
        game.currentPlayer = player1;
        var winner = game.getWinner(player1, piece1, createMove(2, 2, 1, 2));
        assertNull(winner);
    }
}

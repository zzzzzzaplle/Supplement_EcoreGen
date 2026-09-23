import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class GameTieBreakerTests {

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
    @OptionalArcherImplementation
    public void testMoveAndDeadlock() {
        var player1 = new MockPlayer();
        var player2 = new MockPlayer();
        var config = new Configuration(3, new Player[]{player1, player2});
        config.addInitialPiece(createKnight(player1), 0, 0);
        config.addInitialPiece(createArcher(player1), 1, 0);
        config.addInitialPiece(createKnight(player1), 2, 0);
        config.addInitialPiece(createKnight(player2), 0, 2);
        config.addInitialPiece(createArcher(player2), 1, 2);
        config.addInitialPiece(createKnight(player2), 2, 2);
        var game = createJesonMor(config);
        player1.setNextMoves(new Move[]{
                createMove(1, 0, 1, 1),
                createMove(2, 0, 1, 2),
        });
        player2.setNextMoves(new Move[]{
                createMove(0, 2, 1, 0),
        });

        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            var winner = game.start();
            assertEquals(player2, winner);
            assertEquals(4, player1.getScore());
            assertEquals(3, player2.getScore());
            assertEquals(3, game.getNumMoves());
        });
    }
}

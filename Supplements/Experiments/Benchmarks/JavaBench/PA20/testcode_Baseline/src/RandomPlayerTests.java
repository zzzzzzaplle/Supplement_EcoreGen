import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandomPlayerTests {
    private Configuration config;
    private MockPlayer player1;
    private RandomPlayer player2;

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

    private static RandomPlayer createRandomPlayer(String name) {
        RandomPlayer rp = new RandomPlayer();
        rp.setName(name);
        rp.setColor(Color.CYAN);
        return rp;
    }

    private static JesonMor createJesonMor(Configuration config) {
        return new JesonMor(config);
    }

    @BeforeEach
    public void setUpGame() {
        this.player1 = new MockPlayer(Color.PURPLE);
        this.player2 = createRandomPlayer("RandomPlayer");
        this.config = new Configuration(3, new Player[]{player1, player2});
    }

    @Test
    @SampleTest
    public void testNextMove() {
        var piece1 = new MockPiece(player1);
        var piece2 = new MockPiece(player2);
        this.config.addInitialPiece(piece1, 0, 0);
        this.config.addInitialPiece(piece2, 2, 2);
        var game = new JesonMor(this.config);
        var move = player2.nextMove(game, game.getAvailableMoves(player2));
        assertTrue(Arrays.asList(game.getAvailableMoves(player2)).contains(move));
    }
}
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueuedStringInputStream extends InputStream {
    private final List<byte[]> inputs;

    private int currentInputIndex;
    private int currentInputPointer;

    public QueuedStringInputStream(String... inputs) {
        this.inputs = Arrays.stream(inputs).map(String::getBytes).collect(Collectors.toList());
        currentInputIndex = 0;
        currentInputPointer = 0;
    }

    @Override
    public int read() {
        if (currentInputIndex >= this.inputs.size()) {
            // all inputs have been consumed.
            return -1;
        }
        if (currentInputPointer >= this.inputs.get(currentInputIndex).length) {
            // current input finishes, switch to next one
            currentInputIndex++;
            currentInputPointer = 0;
            // pretend input stream reads to the end, but it actually not
            return -1;
        }
        return this.inputs.get(currentInputIndex)[currentInputPointer++];
    }
}

public class ConsolePlayerTests {

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

    private static ConsolePlayer createConsolePlayer(String name) {
        ConsolePlayer cp = new ConsolePlayer();
        cp.setName(name);
        cp.setColor(Color.CYAN);
        return cp;
    }

    private static JesonMor createJesonMor(Configuration config) {
        return new JesonMor(config);
    }

    @Test
    @SampleTest
    public void testNextMove() {
        var stream = new QueuedStringInputStream("c3->c2\r\n"); //correct
        InputStream stdin = System.in;
        try {
            System.setIn(stream);
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = new Configuration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createJesonMor(config);
            var move = player2.nextMove(game, game.getAvailableMoves(player2));
            assertEquals(createMove(2, 2, 2, 1), move);
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput0() {
        var stream = new QueuedStringInputStream("c3->c4\r\n", "c3->b3\r\n");// out of boundary
        InputStream stdin = System.in;
        try {
            System.setIn(stream);
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = new Configuration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createJesonMor(config);
            var move = player2.nextMove(game, game.getAvailableMoves(player2));
            assertEquals(createMove(2, 2, 1, 2), move);
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput1() {
        var stream = new QueuedStringInputStream("1b->2b\r\n", "c3->b3\r\n"); // invalid format
        InputStream stdin = System.in;
        try {
            System.setIn(stream);
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = new Configuration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createJesonMor(config);
            var move = player2.nextMove(game, game.getAvailableMoves(player2));
            assertEquals(createMove(2, 2, 1, 2), move);
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput2() {
        var stream = new QueuedStringInputStream("c3jdakfja\r\n", "c3->b3\r\n");// invalid format
        InputStream stdin = System.in;
        try {
            System.setIn(stream);
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = new Configuration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createJesonMor(config);
            var move = player2.nextMove(game, game.getAvailableMoves(player2));
            assertEquals(createMove(2, 2, 1, 2), move);
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput3() {
        var stream = new QueuedStringInputStream("c3->c3\r\n", "c3->b3\r\n"); // same source and destination
        InputStream stdin = System.in;
        try {
            System.setIn(stream);
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = new Configuration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createJesonMor(config);
            var move = player2.nextMove(game, game.getAvailableMoves(player2));
            assertEquals(createMove(2, 2, 1, 2), move);
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput4() {
        var stream = new QueuedStringInputStream("c11->b11\r\n");  // correct
        InputStream stdin = System.in;
        try {
            System.setIn(stream);
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = new Configuration(11, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 10);
            var game = createJesonMor(config);
            var move = player2.nextMove(game, game.getAvailableMoves(player2));
            assertEquals(createMove(2, 10, 1, 10), move);
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput5() {
        var stream = new QueuedStringInputStream("c0->c3\r\n", "c3->b3\r\n");// index underflow
        InputStream stdin = System.in;
        try {
            System.setIn(stream);
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = new Configuration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createJesonMor(config);
            var move = player2.nextMove(game, game.getAvailableMoves(player2));
            assertEquals(createMove(2, 2, 1, 2), move);
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput6() {
        var stream = new QueuedStringInputStream("c1->c3\r\n", "c3->b3\r\n"); // no piece in source place
        InputStream stdin = System.in;
        try {
            System.setIn(stream);
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = new Configuration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createJesonMor(config);
            var move = player2.nextMove(game, game.getAvailableMoves(player2));
            assertEquals(createMove(2, 2, 1, 2), move);
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput7() {
        var stream = new QueuedStringInputStream("a1->a2\r\n", "c3->b3\r\n"); // not belonging
        InputStream stdin = System.in;
        try {
            System.setIn(stream);
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = new Configuration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createJesonMor(config);
            var move = player2.nextMove(game, game.getAvailableMoves(player2));
            assertEquals(createMove(2, 2, 1, 2), move);
        } finally {
            System.setIn(stdin);
        }
    }
}

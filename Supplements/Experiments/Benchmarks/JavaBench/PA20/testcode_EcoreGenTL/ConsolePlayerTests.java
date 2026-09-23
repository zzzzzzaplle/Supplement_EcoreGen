import edu.pa20.Color;
import edu.pa20.Configuration;
import edu.pa20.ConsolePlayer;
import edu.pa20.JesonMor;
import edu.pa20.Move;
import edu.pa20.Pa20Factory;
import edu.pa20.Piece;
import edu.pa20.Player;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QueuedStringInputStream extends InputStream {
    private final List<String> inputs;
    private int currentInputIndex;
    private int currentInputPointer;
    public QueuedStringInputStream(String... inputs) {
        this.inputs = Arrays.asList(inputs);
        currentInputIndex = 0;
        currentInputPointer = 0;
    }
    @Override
    public int read() {
        if (currentInputIndex >= this.inputs.size()) {
            return -1;
        }
        
        String current = this.inputs.get(currentInputIndex);
        if (currentInputPointer >= current.length()) {
            // 当前字符串结束，切换到下一个
            currentInputIndex++;
            currentInputPointer = 0;
            
            if (currentInputIndex >= this.inputs.size()) {
                return -1;
            }
            
            // 递归读取下一个字符串
            return read();
        }
        
        return this.inputs.get(currentInputIndex).charAt(currentInputPointer++);
    }

    @Override
    public int read(byte[] b, int off, int len) {
        if (currentInputIndex >= this.inputs.size()) {
            return -1;
        }
        if (len == 0) {
            return 0;
        }

        String current = this.inputs.get(currentInputIndex);
        int count = 0;
        while (count < len && currentInputPointer < current.length()) {
            b[off + count] = (byte) current.charAt(currentInputPointer++);
            count++;
        }
        if (currentInputPointer >= current.length()) {
            currentInputIndex++;
            currentInputPointer = 0;
        }
        return count;
    }
}

public class ConsolePlayerTests {
    @Test
    @SampleTest
    public void testNextMove() {
        InputStream stdin = System.in;
        try {
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = createConfiguration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createGame(config);
            var expected = createMove(2, 2, 2, 1);
            var availableMoves = game.getAvailableMoves(player2);
            System.setIn(createAdaptedInput(availableMoves, expected, "c3->c2\r\n")); // correct
            var move = player2.nextMove(game, availableMoves);
            assertTrue(move.equal(expected));
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput0() {
        InputStream stdin = System.in;
        try {
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = createConfiguration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createGame(config);
            var expected = createMove(2, 2, 1, 2);
            var availableMoves = game.getAvailableMoves(player2);
            System.setIn(createAdaptedInput(availableMoves, expected, "c3->c4\r\n", "c3->b3\r\n"));// out of boundary
            var move = player2.nextMove(game, availableMoves);
            assertTrue(move.equal(expected));
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput1() {
        InputStream stdin = System.in;
        try {
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = createConfiguration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createGame(config);
            var expected = createMove(2, 2, 1, 2);
            var availableMoves = game.getAvailableMoves(player2);
            System.setIn(createAdaptedInput(availableMoves, expected, "1b->2b\r\n", "c3->b3\r\n")); // invalid format
            var move = player2.nextMove(game, availableMoves);
            assertTrue(move.equal(expected));
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput2() {
        InputStream stdin = System.in;
        try {
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = createConfiguration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createGame(config);
            var expected = createMove(2, 2, 1, 2);
            var availableMoves = game.getAvailableMoves(player2);
            System.setIn(createAdaptedInput(availableMoves, expected, "c3jdakfja\r\n", "c3->b3\r\n"));// invalid format
            var move = player2.nextMove(game, availableMoves);
            assertTrue(move.equal(expected));
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput3() {
        InputStream stdin = System.in;
        try {
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = createConfiguration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createGame(config);
            var expected = createMove(2, 2, 1, 2);
            var availableMoves = game.getAvailableMoves(player2);
            System.setIn(createAdaptedInput(availableMoves, expected, "c3->c3\r\n", "c3->b3\r\n")); // same source and destination
            var move = player2.nextMove(game, availableMoves);
            assertTrue(move.equal(expected));
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput4() {
        InputStream stdin = System.in;
        try {
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = createConfiguration(11, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 10);
            var game = createGame(config);
            var expected = createMove(2, 10, 1, 10);
            var availableMoves = game.getAvailableMoves(player2);
            System.setIn(createAdaptedInput(availableMoves, expected, "c11->b11\r\n"));  // correct
            var move = player2.nextMove(game, availableMoves);
            assertTrue(move.equal(expected));
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput5() {
        InputStream stdin = System.in;
        try {
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = createConfiguration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createGame(config);
            var expected = createMove(2, 2, 1, 2);
            var availableMoves = game.getAvailableMoves(player2);
            System.setIn(createAdaptedInput(availableMoves, expected, "c0->c3\r\n", "c3->b3\r\n"));// index underflow
            var move = player2.nextMove(game, availableMoves);
            assertTrue(move.equal(expected));
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput6() {
        InputStream stdin = System.in;
        try {
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = createConfiguration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createGame(config);
            var expected = createMove(2, 2, 1, 2);
            var availableMoves = game.getAvailableMoves(player2);
            System.setIn(createAdaptedInput(availableMoves, expected, "c1->c3\r\n", "c3->b3\r\n")); // no piece in source place
            var move = player2.nextMove(game, availableMoves);
            assertTrue(move.equal(expected));
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    @UnitTest
    public void testNextMoveInvalidInput7() {
        InputStream stdin = System.in;
        try {
            var player1 = new MockPlayer(Color.PURPLE);
            var player2 = createConsolePlayer("RandomPlayer");
            var config = createConfiguration(3, new Player[]{player1, player2});
            var piece1 = new MockPiece(player1);
            var piece2 = new MockPiece(player2);
            config.addInitialPiece(piece1, 0, 0);
            config.addInitialPiece(piece2, 2, 2);
            var game = createGame(config);
            var expected = createMove(2, 2, 1, 2);
            var availableMoves = game.getAvailableMoves(player2);
            System.setIn(createAdaptedInput(availableMoves, expected, "a1->a2\r\n", "c3->b3\r\n")); // not belonging
            var move = player2.nextMove(game, availableMoves);
            assertTrue(move.equal(expected));
        } finally {
            System.setIn(stdin);
        }
    }

    private static QueuedStringInputStream createAdaptedInput(EList<Move> availableMoves, Move expected, String... coordinateInputs) {
        String[] inputs = Arrays.copyOf(coordinateInputs, coordinateInputs.length + 2);
        int index = inputIndexForMove(availableMoves, expected);
        inputs[coordinateInputs.length] = " " + (index + 1) + "\n";
        inputs[coordinateInputs.length + 1] = index + "\n";
        return new QueuedStringInputStream(inputs);
    }

    private static int inputIndexForMove(EList<Move> availableMoves, Move expected) {
        for (int i = 0; i < availableMoves.size(); i++) {
            if (availableMoves.get(i).equal(expected)) {
                return i;
            }
        }
        throw new AssertionError("Expected move not found in available moves");
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

    private static ConsolePlayer createConsolePlayer(String name) {
        ConsolePlayer player = Pa20Factory.eINSTANCE.createConsolePlayer();
        player.setName(name);
        return player;
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

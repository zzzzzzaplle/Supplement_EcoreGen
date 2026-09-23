import edu.pa22.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestExtension.class)
class GameStateTest {

    @Tag(TestKind.HIDDEN)
    @Test
    void testMapCopying() {
        final var maxWidth = 2333;
        final var maxHeight = 2333;
        final var random = new Random();
        final var randomEntities = Stream.generate(() -> positionOf(random.nextInt(maxWidth), random.nextInt(maxHeight)))
                .distinct()
                .limit(100)
                .collect(Collectors.toMap(Function.identity(), it -> generateEntity(it.getX())));

        final var firstPos = randomEntities.keySet().stream().findFirst();
        assertTrue(firstPos.isPresent());
        final var gameMap = createGameMap(maxWidth, maxHeight, Collections.singleton(firstPos.get()), 233);
        randomEntities.forEach((p, e) -> gameMap.getMap().put(p, e));

        final var gameState = createGameState(gameMap);

        final var randomPosition = randomEntities.keySet().stream().findAny();
        assertTrue(randomPosition.isPresent());
        gameMap.getMap().put(randomPosition.get(), createEmpty());

        randomEntities.forEach((p, e) -> assertEquals(e, gameState.getEntity(p)));
        assertEquals(233, gameState.getUndoQuota());
        assertEquals(2333, gameState.getBoardHeight());
        assertEquals(2333, gameState.getBoardWidth());
        assertEquals(1, gameState.getDestinations().size());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testAllPlayerIds() {
        final var testMap = TestHelper.parseGameMap("""
                233
                ######
                #APp@#
                #xXa@@#
                ######
                """);
        final var gameState = createGameState(testMap);

        assertEquals(
                new HashSet<>(Arrays.asList(positionOf(1, 1), positionOf(2, 1), positionOf(2, 2))),
                asSet(gameState.getAllPlayerPositions())
        );
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testWin() {
        final var testMap = TestHelper.parseGameMap("""
                233
                ######
                #A.a@#
                #..a@#
                ######
                """);
        final var gameState = createGameState(testMap);
        gameState.move(positionOf(3, 1), positionOf(4, 1));
        gameState.move(positionOf(3, 2), positionOf(4, 2));

        assertTrue(gameState.isWin());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testMove() {
        final var gameState = createGameState(TestHelper.parseGameMap("""
                233
                ######
                #A.a@#
                #..a@#
                ######
                """
        ));

        gameState.move(positionOf(1, 1), positionOf(2, 1));
        assertEquals(positionOf(2, 1), gameState.getPlayerPositionById(0));
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testPushBox() {
        final var gameState = createGameState(TestHelper.parseGameMap("""
                233
                ######
                #.Aa@#
                #..a@#
                ######
                """
        ));

        gameState.move(positionOf(3, 1), positionOf(4, 1));
        gameState.move(positionOf(2, 1), positionOf(3, 1));

        assertEquals(positionOf(3, 1), gameState.getPlayerPositionById(0));
        assertTrue(gameState.getEntity(positionOf(4, 1)) instanceof Box);
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testGetUndoLimit() {
        final var gameState = createGameState(TestHelper.parseGameMap("""
                233
                ######
                #.Aa@#
                #..a@#
                ######
                """
        ));
        assertEquals(233, gameState.getUndoQuota());
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testGetUndoUnlimited() {
        final var gameState = createGameState(TestHelper.parseGameMap("""
                -1
                ######
                #.Aa@#
                #..a@#
                ######
                """
        ));
        // EMF version returns int, -1 means unlimited
        assertEquals(-1, gameState.getUndoQuota());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testUndoWhenThereIsCheckpoint() {
        final var gameState = createGameState(TestHelper.parseGameMap("""
                233
                ######
                #.Aa@#
                #..a@#
                ######
                """
        ));
        gameState.move(positionOf(3, 1), positionOf(4, 1));
        gameState.move(positionOf(2, 1), positionOf(3, 1));
        gameState.checkpoint();

        gameState.undo();
        assertEquals(positionOf(2, 1), gameState.getPlayerPositionById(0));
        assertTrue(gameState.getEntity(positionOf(3, 1)) instanceof Box);
        assertTrue(gameState.getEntity(positionOf(4, 1)) instanceof Empty);

        assertEquals(232, gameState.getUndoQuota());
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testUndoWhenThereIsMoveButNoCheckpoint() {
        final var gameState = createGameState(TestHelper.parseGameMap("""
                233
                ######
                #A.a@#
                #..a@#
                ######
                """
        ));
        gameState.move(positionOf(1, 1), positionOf(2, 1));

        gameState.undo();

        assertEquals(233, gameState.getUndoQuota());
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testUndoWhenThereIsNoMove() {
        final var gameState = createGameState(TestHelper.parseGameMap("""
                233
                ######
                #A.a@#
                #..a@#
                ######
                """
        ));
        gameState.undo();

        assertEquals(233, gameState.getUndoQuota());
    }

    // ==================== Helper Methods ====================

    /**
     * Creates a GameState from a GameMap, initializing all necessary fields.
     * This replaces the original constructor `new GameState(GameMap)`.
     */
    private static GameState createGameState(GameMap gameMap) {
        GameState gameState = Pa22Factory.eINSTANCE.createGameState();

        // Copy entities from gameMap
        gameState.setEntities(new java.util.HashMap<>(gameMap.getMap()));

        // Copy destinations
        gameState.getDestinations().addAll(gameMap.getDestinations());

        // Set dimensions
        gameState.setBoardWidth(gameMap.getMaxWidth());
        gameState.setBoardHeight(gameMap.getMaxHeight());

        // Set undo quota
        int undoLimit = gameMap.getUndoLimit();
        gameState.setUndoQuota(undoLimit);

        // Initialize current transition
        gameState.setCurrentTransition(Pa22Factory.eINSTANCE.createGameStateTransition());

        // Initialize history stack (required for undo/checkpoint)
        gameState.setHistory(new java.util.Stack<>());

        return gameState;
    }

    /**
     * Creates a GameMap with the specified parameters.
     */
    private static GameMap createGameMap(int maxWidth, int maxHeight, java.util.Set<Position> destinations, int undoLimit) {
        GameMap gameMap = Pa22Factory.eINSTANCE.createGameMap();
        gameMap.setMaxWidth(maxWidth);
        gameMap.setMaxHeight(maxHeight);
        gameMap.setUndoLimit(undoLimit);
        gameMap.setMap(new java.util.HashMap<>());
        gameMap.getDestinations().addAll(destinations);
        return gameMap;
    }

    /**
     * Creates a Position at the specified coordinates.
     */
    private static Position positionOf(int x, int y) {
        Position position = Pa22Factory.eINSTANCE.createPosition();
        position.setX(x);
        position.setY(y);
        return position;
    }

    /**
     * Creates an Empty entity.
     */
    private static Empty createEmpty() {
        return Pa22Factory.eINSTANCE.createEmpty();
    }

    private static <T> HashSet<T> asSet(Iterable<T> values) {
        HashSet<T> result = new HashSet<>();
        values.forEach(result::add);
        return result;
    }

    /**
     * Generates an entity based on the key.
     */
    private Entity generateEntity(int key) {
        return switch (key % 4) {
            case 0 -> {
                Box box = Pa22Factory.eINSTANCE.createBox();
                box.setPlayerId(0);
                yield box;
            }
            case 1 -> Pa22Factory.eINSTANCE.createEmpty();
            case 2 -> {
                Player player = Pa22Factory.eINSTANCE.createPlayer();
                player.setId(0);
                yield player;
            }
            case 3 -> Pa22Factory.eINSTANCE.createWall();
            default -> throw new RuntimeException("Should not reach here");
        };
    }
}

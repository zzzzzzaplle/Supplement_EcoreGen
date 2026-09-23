import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import java.util.List;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class GameBoardControllerTest {

    private GameBoard gameBoard = null;
    private GameBoardController controller = null;

    private static Position createPosition(int row, int col) {
        Position p = new Position();
        p.setRow(row);
        p.setCol(col);
        return p;
    }

    private static EntityCell createEntityCell(Position pos) {
        EntityCell c = new EntityCell();
        c.setPosition(pos);
        return c;
    }

    private static EntityCell createEntityCell(Position pos, Entity entity) {
        EntityCell c = createEntityCell(pos);
        c.setentity(entity);
        return c;
    }

    private static Wall createWall(Position pos) {
        Wall w = new Wall();
        w.setPosition(pos);
        return w;
    }

    private static StopCell createStopCell(Position pos) {
        StopCell s = new StopCell();
        s.setPosition(pos);
        return s;
    }

    private static StopCell createStopCell(Position pos, Entity entity) {
        StopCell s = createStopCell(pos);
        s.setentity(entity);
        return s;
    }

    private static Alive createAlive(Position newPos, Position origPos, List<Position> gems, List<Position> lives) {
        Alive a = new Alive();
        a.setNewPosition(newPos);
        a.setOrigPosition(origPos);
        a.setCollectedGems(gems);
        a.setCollectedExtraLives(lives);
        return a;
    }

    private static GameBoardController createGameBoardController(GameBoard board) {
        GameBoardController c = new GameBoardController();
        c.setGameBoard(board);
        return c;
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = GameBoardController.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    // For UP and LEFT:
    // P.*
    // ...
    // ...
    //
    // For DOWN and RIGHT:
    // ..*
    // ...
    // ..P
    @ParameterizedTest
    @Tag("provided")
    @EnumSource(value = Direction.class)
    @DisplayName("Make Move - Move to Adjacent Border")
    void testMakeMoveToBorder(final Direction direction) {
        final Position expectedPos;
        if (direction == Direction.UP || direction == Direction.LEFT) {
            expectedPos = createPosition(0, 0);
        } else {
            expectedPos = createPosition(2, 2);
        }

        gameBoard = GameBoardUtils.createGameBoard(3, 3, pos -> {
            if (pos.equals(expectedPos)) {
                return createEntityCell(pos, new Player());
            } else if (pos.equals(createPosition(0, 2))) {
                return createEntityCell(pos, new Gem());
            } else {
                return createEntityCell(pos);
            }
        });
        controller = createGameBoardController(gameBoard);

        assumeTrue(
            gameBoard.getEntityCell(expectedPos).getEntity() instanceof Player
        );
        assumeTrue(
            gameBoard
                .getPlayer()
                .equals(gameBoard.getEntityCell(expectedPos).getEntity())
        );
        assumeTrue(gameBoard.getNumGems() == 1);
        assumeTrue(gameBoard.getEntityCell(0, 2).getEntity() instanceof Gem);

        final var moveResult = controller.makeMove(direction);

        assertTrue(moveResult instanceof Invalid);

        final var invalidResult = (Invalid) moveResult;

        // Result Assertions
        assertEquals(expectedPos, invalidResult.getNewPosition());

        // Non-Mutation Assertions
        assertEquals(
            gameBoard.getPlayer(),
            gameBoard.getEntityCell(expectedPos).getEntity()
        );
        assertEquals(1, gameBoard.getNumGems());
        assertTrue(gameBoard.getEntityCell(0, 2).getEntity() instanceof Gem);
    }

    // P.W*
    // ....
    @Test
    @Tag("provided")
    @DisplayName("Make Move - Move to Wall")
    void testMakeValidMoveToWall() {
        gameBoard = GameBoardUtils.createGameBoard(2, 4, pos -> {
            if (pos.equals(createPosition(0, 0))) {
                return createEntityCell(pos, new Player());
            } else if (pos.equals(createPosition(0, 2))) {
                return createWall(pos);
            } else if (pos.equals(createPosition(0, 3))) {
                return createEntityCell(pos, new Gem());
            } else {
                return createEntityCell(pos);
            }
        });
        controller = createGameBoardController(gameBoard);

        assumeTrue(gameBoard.getEntityCell(0, 0).getEntity() instanceof Player);
        assumeTrue(
            gameBoard
                .getPlayer()
                .equals(gameBoard.getEntityCell(0, 0).getEntity())
        );
        assumeTrue(gameBoard.getNumGems() == 1);
        assumeTrue(gameBoard.getEntityCell(0, 3).getEntity() instanceof Gem);

        final var moveResult = controller.makeMove(Direction.RIGHT);

        assertTrue(moveResult instanceof Alive);

        final var aliveResult = (Alive) moveResult;

        // Result Assertions
        assertEquals(createPosition(0, 1), aliveResult.getNewPosition());
        assertEquals(createPosition(0, 0), aliveResult.getOrigPosition());
        assertTrue(aliveResult.getCollectedGems().isEmpty());
        assertTrue(aliveResult.getCollectedExtraLives().isEmpty());

        // GameBoard Mutation Assertions
        assertNull(gameBoard.getEntityCell(0, 0).getEntity());
        assertEquals(
            gameBoard.getPlayer(),
            gameBoard.getEntityCell(0, 1).getEntity()
        );

        // Non-Mutation Assertions
        assertEquals(1, gameBoard.getNumGems());
        assertTrue(gameBoard.getEntityCell(0, 3).getEntity() instanceof Gem);
    }

    // P.#.
    // ...*
    @Test
    @Tag("provided")
    @DisplayName("Make Move - Move to StopCell")
    void testMakeValidMoveToStopCell() {
        gameBoard = GameBoardUtils.createGameBoard(2, 4, pos -> {
            if (pos.equals(createPosition(0, 0))) {
                return createEntityCell(pos, new Player());
            } else if (pos.equals(createPosition(0, 2))) {
                return createStopCell(pos);
            } else if (pos.equals(createPosition(1, 3))) {
                return createEntityCell(pos, new Gem());
            } else {
                return createEntityCell(pos);
            }
        });
        controller = createGameBoardController(gameBoard);

        assumeTrue(gameBoard.getEntityCell(0, 0).getEntity() instanceof Player);
        assumeTrue(
            gameBoard
                .getPlayer()
                .equals(gameBoard.getEntityCell(0, 0).getEntity())
        );
        assumeTrue(gameBoard.getNumGems() == 1);
        assumeTrue(gameBoard.getEntityCell(1, 3).getEntity() instanceof Gem);

        final var moveResult = controller.makeMove(Direction.RIGHT);

        assertTrue(moveResult instanceof Alive);

        final var aliveResult = (Alive) moveResult;

        // Result Assertions
        assertEquals(createPosition(0, 2), aliveResult.getNewPosition());
        assertEquals(createPosition(0, 0), aliveResult.getOrigPosition());
        assertTrue(aliveResult.getCollectedGems().isEmpty());
        assertTrue(aliveResult.getCollectedExtraLives().isEmpty());

        // GameBoard Mutation Assertions
        assertNull(gameBoard.getEntityCell(0, 0).getEntity());
        assertEquals(
            gameBoard.getPlayer(),
            gameBoard.getEntityCell(0, 2).getEntity()
        );

        // Non-Mutation Assertions
        assertEquals(1, gameBoard.getNumGems());
        assertTrue(gameBoard.getEntityCell(1, 3).getEntity() instanceof Gem);
    }

    // P**#
    @Test
    @Tag("provided")
    @DisplayName("Make Move - Move passes Gem")
    void testMakeValidMovePassingGem() {
        gameBoard = GameBoardUtils.createGameBoard(1, 4, pos -> {
            if (pos.equals(createPosition(0, 0))) {
                return createEntityCell(pos, new Player());
            } else if (
                pos.equals(createPosition(0, 1)) || pos.equals(createPosition(0, 2))
            ) {
                return createEntityCell(pos, new Gem());
            } else if (pos.equals(createPosition(0, 3))) {
                return createStopCell(pos);
            } else {
                return createEntityCell(pos);
            }
        });
        controller = createGameBoardController(gameBoard);

        assumeTrue(gameBoard.getEntityCell(0, 0).getEntity() instanceof Player);
        assumeTrue(
            gameBoard
                .getPlayer()
                .equals(gameBoard.getEntityCell(0, 0).getEntity())
        );
        assumeTrue(gameBoard.getNumGems() == 2);
        assumeTrue(gameBoard.getEntityCell(0, 1).getEntity() instanceof Gem);
        assumeTrue(gameBoard.getEntityCell(0, 2).getEntity() instanceof Gem);

        final var moveResult = controller.makeMove(Direction.RIGHT);

        assertTrue(moveResult instanceof Alive);

        final var aliveResult = (Alive) moveResult;

        // Result Assertions
        assertEquals(createPosition(0, 3), aliveResult.getNewPosition());
        assertEquals(createPosition(0, 0), aliveResult.getOrigPosition());
        assertEquals(2, aliveResult.getCollectedGems().size());
        assertTrue(aliveResult.getCollectedGems().contains(createPosition(0, 1)));
        assertTrue(aliveResult.getCollectedGems().contains(createPosition(0, 2)));
        assertTrue(aliveResult.getCollectedExtraLives().isEmpty());

        // GameBoard Mutation Assertions
        assertNull(gameBoard.getEntityCell(0, 0).getEntity());
        assertNull(gameBoard.getEntityCell(0, 1).getEntity());
        assertNull(gameBoard.getEntityCell(0, 2).getEntity());
        assertEquals(
            gameBoard.getPlayer(),
            gameBoard.getEntityCell(0, 3).getEntity()
        );

        // Non-Mutation Assertions
        assertEquals(0, gameBoard.getNumGems());
    }

    // PLL#
    // ...*
    @Test
    @Tag("provided")
    @DisplayName("Make Move - Move passes ExtraLives")
    void testMakeValidMovePassingExtraLives() {
        gameBoard = GameBoardUtils.createGameBoard(2, 4, pos -> {
            if (pos.equals(createPosition(0, 0))) {
                return createEntityCell(pos, new Player());
            } else if (
                pos.equals(createPosition(0, 1)) || pos.equals(createPosition(0, 2))
            ) {
                return createEntityCell(pos, new ExtraLife());
            } else if (pos.equals(createPosition(0, 3))) {
                return createStopCell(pos);
            } else if (pos.equals(createPosition(1, 3))) {
                return createEntityCell(pos, new Gem());
            } else {
                return createEntityCell(pos);
            }
        });
        controller = createGameBoardController(gameBoard);

        assumeTrue(gameBoard.getEntityCell(0, 0).getEntity() instanceof Player);
        assumeTrue(
            gameBoard
                .getPlayer()
                .equals(gameBoard.getEntityCell(0, 0).getEntity())
        );
        assumeTrue(gameBoard.getNumGems() == 1);
        assumeTrue(gameBoard.getEntityCell(1, 3).getEntity() instanceof Gem);
        assumeTrue(
            gameBoard.getEntityCell(0, 1).getEntity() instanceof ExtraLife
        );
        assumeTrue(
            gameBoard.getEntityCell(0, 2).getEntity() instanceof ExtraLife
        );

        final var moveResult = controller.makeMove(Direction.RIGHT);

        assertTrue(moveResult instanceof Alive);

        final var aliveResult = (Alive) moveResult;

        // Result Assertions
        assertEquals(createPosition(0, 3), aliveResult.getNewPosition());
        assertEquals(createPosition(0, 0), aliveResult.getOrigPosition());
        assertTrue(aliveResult.getCollectedGems().isEmpty());
        assertEquals(2, aliveResult.getCollectedExtraLives().size());
        assertTrue(
            aliveResult.getCollectedExtraLives().contains(createPosition(0, 1))
        );
        assertTrue(
            aliveResult.getCollectedExtraLives().contains(createPosition(0, 2))
        );

        // GameBoard Mutation Assertions
        assertNull(gameBoard.getEntityCell(0, 0).getEntity());
        assertNull(gameBoard.getEntityCell(0, 1).getEntity());
        assertNull(gameBoard.getEntityCell(0, 2).getEntity());
        assertEquals(
            gameBoard.getPlayer(),
            gameBoard.getEntityCell(0, 3).getEntity()
        );

        // Non-Mutation Assertions
        assertEquals(1, gameBoard.getNumGems());
        assertTrue(gameBoard.getEntityCell(1, 3).getEntity() instanceof Gem);
    }

    // P.M
    // ..*
    @Test
    @Tag("provided")
    @DisplayName("Make Move - Move hits Mine")
    void testMakeValidMoveToMine() {
        gameBoard = GameBoardUtils.createGameBoard(2, 3, pos -> {
            if (pos.equals(createPosition(0, 0))) {
                return createEntityCell(pos, new Player());
            } else if (pos.equals(createPosition(0, 2))) {
                return createEntityCell(pos, new Mine());
            } else if (pos.equals(createPosition(1, 2))) {
                return createEntityCell(pos, new Gem());
            } else {
                return createEntityCell(pos);
            }
        });
        controller = createGameBoardController(gameBoard);

        assumeTrue(gameBoard.getEntityCell(0, 0).getEntity() instanceof Player);
        assumeTrue(
            gameBoard
                .getPlayer()
                .equals(gameBoard.getEntityCell(0, 0).getEntity())
        );
        assumeTrue(gameBoard.getNumGems() == 1);
        assumeTrue(gameBoard.getEntityCell(1, 2).getEntity() instanceof Gem);
        assumeTrue(gameBoard.getEntityCell(0, 2).getEntity() instanceof Mine);

        final var moveResult = controller.makeMove(Direction.RIGHT);

        assertTrue(moveResult instanceof Dead);

        final var deadResult = (Dead) moveResult;

        // Result Assertions
        assertEquals(createPosition(0, 0), deadResult.getNewPosition());
        assertEquals(createPosition(0, 0), deadResult.getOrigPosition());

        // Non-Mutation Assertions
        assertEquals(
            gameBoard.getPlayer(),
            gameBoard.getEntityCell(0, 0).getEntity()
        );
        assertTrue(gameBoard.getEntityCell(0, 2).getEntity() instanceof Mine);
        assertEquals(1, gameBoard.getNumGems());
        assertTrue(gameBoard.getEntityCell(1, 2).getEntity() instanceof Gem);
    }

    // Undoes the following move:
    // P.# -> ..P
    // ..*    ..*
    @Test
    @Tag("provided")
    @DisplayName("Undo Move - Simple")
    void testUndoMoveTrivial() {
        gameBoard = GameBoardUtils.createGameBoard(2, 3, pos -> {
            if (pos.equals(createPosition(0, 2))) {
                return createStopCell(pos, new Player());
            } else if (pos.equals(createPosition(1, 2))) {
                return createEntityCell(pos, new Gem());
            } else {
                return createEntityCell(pos);
            }
        });
        controller = createGameBoardController(gameBoard);

        assumeTrue(gameBoard.getEntityCell(0, 2).getEntity() instanceof Player);
        assumeTrue(
            gameBoard
                .getPlayer()
                .equals(gameBoard.getEntityCell(0, 2).getEntity())
        );
        assumeTrue(gameBoard.getNumGems() == 1);
        assumeTrue(gameBoard.getEntityCell(1, 2).getEntity() instanceof Gem);

        final var moveToUndo = createAlive(
            createPosition(0, 2),
            createPosition(0, 0),
            Collections.emptyList(),
            Collections.emptyList()
        );

        controller.undoMove(moveToUndo);

        // Mutation Assertions
        assertNull(gameBoard.getEntityCell(0, 2).getEntity());
        assertEquals(
            gameBoard.getPlayer(),
            gameBoard.getEntityCell(0, 0).getEntity()
        );

        // Non-Mutation Assertions
        assertTrue(gameBoard.getEntityCell(0, 2) instanceof StopCell);
        assertTrue(gameBoard.getEntityCell(1, 2).getEntity() instanceof Gem);
    }

    // Undoes the following move:
    // P*L# -> ...P
    // ...*    ...*
    @Test
    @Tag("actual")
    @DisplayName("Undo Move - Restores Entities")
    void testUndoMoveWithPickups() {
        gameBoard = GameBoardUtils.createGameBoard(2, 4, pos -> {
            if (pos.equals(createPosition(0, 3))) {
                return createStopCell(pos, new Player());
            } else if (pos.equals(createPosition(1, 3))) {
                return createEntityCell(pos, new Gem());
            } else {
                return createEntityCell(pos);
            }
        });
        controller = createGameBoardController(gameBoard);

        assumeTrue(gameBoard.getEntityCell(0, 3).getEntity() instanceof Player);
        assumeTrue(
            gameBoard
                .getPlayer()
                .equals(gameBoard.getEntityCell(0, 3).getEntity())
        );
        assumeTrue(gameBoard.getNumGems() == 1);
        assumeTrue(gameBoard.getEntityCell(1, 3).getEntity() instanceof Gem);

        final var moveToUndo = createAlive(
            createPosition(0, 3),
            createPosition(0, 0),
            Collections.singletonList(createPosition(0, 1)),
            Collections.singletonList(createPosition(0, 2))
        );

        controller.undoMove(moveToUndo);

        // Mutation Assertions
        assertNull(gameBoard.getEntityCell(0, 3).getEntity());
        assertTrue(
            gameBoard.getEntityCell(0, 2).getEntity() instanceof ExtraLife
        );
        assertTrue(gameBoard.getEntityCell(0, 1).getEntity() instanceof Gem);
        assertEquals(
            gameBoard.getPlayer(),
            gameBoard.getEntityCell(0, 0).getEntity()
        );

        // Non-Mutation Assertions
        assertTrue(gameBoard.getEntityCell(0, 3) instanceof StopCell);
        assertTrue(gameBoard.getEntityCell(1, 3).getEntity() instanceof Gem);
    }

    @AfterEach
    void tearDown() {
        controller = null;
        gameBoard = null;
    }
}

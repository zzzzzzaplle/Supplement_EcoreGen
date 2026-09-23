import edu.pa22.*;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(TestExtension.class)
class AbstractSokobanGameTest {

    @Tag(TestKind.HIDDEN)
    @Test
    void testInvalidInput() {
        final var game = new SokobanGameForTesting(mock(GameState.class));
        final var message = String.valueOf(new Random().nextLong());

        final var action = createInvalidInput(-1, message);
        final var result = game.feedActionForProcessing(action);

        assertTrue(result instanceof Failed);
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testExceedingUndoQuota() {
        final var gameState = mock(GameState.class);
        when(gameState.getPlayerPositionById(anyInt())).thenReturn(positionOf(0, 0));
        when(gameState.getUndoQuota()).thenReturn(0);

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(createUndo(0));

        assertTrue(result instanceof Failed);
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testUndoWithinQuota() {
        final var gameState = mock(GameState.class);
        when(gameState.getPlayerPositionById(anyInt())).thenReturn(positionOf(0, 0));
        when(gameState.getUndoQuota()).thenReturn(1);

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(createUndo(0));

        verify(gameState, times(1)).undo();
        assertTrue(result instanceof Success);
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testUndoUnlimited() {
        final var gameState = mock(GameState.class);
        when(gameState.getPlayerPositionById(anyInt())).thenReturn(positionOf(0, 0));
        when(gameState.getUndoQuota()).thenReturn(-1);

        final var game = new SokobanGameForTesting(gameState);
        for (int i = 0; i < 10000; i++) {
            final var result = game.feedActionForProcessing(createUndo(0));
            assertTrue(result instanceof Success);
        }
        verify(gameState, times(10000)).undo();
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testExit() {
        final var gameState = mock(GameState.class);

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(createExit(0));

        assertTrue(result instanceof Success);
        assertTrue(game.shouldStop());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testMove() {
        String mapText = """
                233
                ######
                #A..@#
                #....#
                #a...#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(createGameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(createMoveDown(0));

        assertTrue(result instanceof Success);
        verify(gameState, times(1)).move(any(), any());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testHitWall() {
        String mapText = """
                233
                ######
                #A..@#
                ##...#
                #a...#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(createGameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(createMoveDown(0));

        assertTrue(result instanceof Failed);
        verify(gameState, never()).move(any(), any());
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testHitAnotherPlayer() {
        String mapText = """
                233
                ######
                #A..@#
                #B...#
                #ab.@#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(createGameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(createMoveDown(0));

        assertTrue(result instanceof Failed);
        verify(gameState, never()).move(any(), any());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testPushBox() {
        String mapText = """
                233
                ######
                #A..@#
                #a...#
                #....#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(createGameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(createMoveDown(0));

        assertTrue(result instanceof Success);
        verify(gameState, times(2)).move(any(), any());
        verify(gameState, times(1)).checkpoint();
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testPushBoxAgainstWall() {
        String mapText = """
                233
                ######
                #A..@#
                #a...#
                ##...#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(createGameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(createMoveDown(0));

        assertTrue(result instanceof Failed);
        verify(gameState, never()).move(any(), any());
    }


    @Tag(TestKind.HIDDEN)
    @Test
    void testPushOtherPlayerBox() {
        String mapText = """
                233
                ######
                #AB.@#
                #b.a.#
                #@...#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(createGameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(createMoveDown(0));

        assertTrue(result instanceof Failed);
        verify(gameState, never()).move(any(), any());
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testMoveNonExistingPlayer() {
        String mapText = """
                233
                ######
                #A..@#
                #a...#
                ##...#
                ######
                """;
        final var testMap = TestHelper.parseGameMap(mapText);
        final var gameState = spy(createGameState(testMap));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(createMoveDown(23));

        assertTrue(result instanceof Failed);
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testShouldStopWhenWin() {
        final var gameState = mock(GameState.class);
        when(gameState.isWin()).thenReturn(true);

        final var game = new SokobanGameForTesting(gameState);
        assertTrue(game.shouldStop());
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testCheckpointWhenNeed() {
        final var gameState = spy(createGameState(TestHelper.parseGameMap("""
                233
                ######
                #.Aa@#
                #..a@#
                ######
                """
        )));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(createMoveRight(0));

        verify(gameState, times(1)).checkpoint();
        assertTrue(result instanceof Success);
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testCheckpointWhenNotNeed() {
        final var gameState = spy(createGameState(TestHelper.parseGameMap("""
                233
                ######
                #A.a@#
                #..a@#
                ######
                """
        )));

        final var game = new SokobanGameForTesting(gameState);
        final var result = game.feedActionForProcessing(createMoveRight(0));

        verify(gameState, never()).checkpoint();
        assertTrue(result instanceof Success);
    }

    // ==================== Helper Methods ====================

    /**
     * Creates a GameState from a GameMap, initializing all necessary fields.
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
     * Creates a Position at the specified coordinates.
     */
    private static Position positionOf(int x, int y) {
        Position position = Pa22Factory.eINSTANCE.createPosition();
        position.setX(x);
        position.setY(y);
        return position;
    }

    /**
     * Creates an InvalidInput action.
     */
    private static InvalidInput createInvalidInput(int initiator, String message) {
        InvalidInput action = Pa22Factory.eINSTANCE.createInvalidInput();
        action.setInitiator(initiator);
        action.setMessage(message);
        return action;
    }

    /**
     * Creates an Undo action.
     */
    private static Undo createUndo(int initiator) {
        Undo action = Pa22Factory.eINSTANCE.createUndo();
        action.setInitiator(initiator);
        return action;
    }

    /**
     * Creates an Exit action.
     */
    private static Exit createExit(int initiator) {
        Exit action = Pa22Factory.eINSTANCE.createExit();
        action.setInitiator(initiator);
        return action;
    }

    /**
     * Creates a Move.Down action.
     */
    private static Down createMoveDown(int initiator) {
        Down action = Pa22Factory.eINSTANCE.createDown();
        action.setInitiator(initiator);
        return action;
    }

    /**
     * Creates a Move.Right action.
     */
    private static Right createMoveRight(int initiator) {
        Right action = Pa22Factory.eINSTANCE.createRight();
        action.setInitiator(initiator);
        return action;
    }

    // ==================== Test Helper Class ====================

    private static class SokobanGameForTesting extends AbstractSokobanGame {

        protected SokobanGameForTesting(GameState gameState) {
            this.setState(gameState);
        }

        @Override
        public void run() {
        }

        public ActionResult feedActionForProcessing(Action action) {
            return processAction(action);
        }
    }
}

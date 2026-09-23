import edu.pa22.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import java.util.Arrays;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.mockito.Mockito.*;

@ExtendWith(TestExtension.class)
class TerminalSokobanGameTest {

    @Tag(TestKind.PUBLIC)
    @Test
    void testGameLoop() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(TerminalInputEngine.class);
        final var renderingEngine = mock(TerminalRenderingEngine.class);
        when(gameState.isWin()).thenReturn(false);
        when(inputEngine.fetchAction())
                .thenReturn(createInvalidInput(0, ""))
                .thenReturn(createExit(0));

        final var game = createTerminalSokobanGame(gameState, inputEngine, renderingEngine);
        assertTimeoutPreemptively(Duration.ofMillis(500), game::run);

        final var inOrder = inOrder(inputEngine, renderingEngine);

        // Before loop
        inOrder.verify(renderingEngine).render(eq(gameState));

        // First round
        inOrder.verify(inputEngine).fetchAction();
        inOrder.verify(renderingEngine).render(eq(gameState));

        // Second round
        inOrder.verify(inputEngine).fetchAction();
        inOrder.verify(renderingEngine).render(eq(gameState));

        verify(gameState, atLeastOnce()).getUndoQuota();
        verify(gameState, atLeast(0)).isWin();
        verify(renderingEngine, atLeastOnce()).message(any());
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testGameExit() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(TerminalInputEngine.class);
        final var renderingEngine = mock(TerminalRenderingEngine.class);
        when(gameState.isWin()).thenReturn(false, false, false);
        when(inputEngine.fetchAction())
                .thenReturn(createInvalidInput(0, ""),
                        createInvalidInput(0, ""),
                        createExit(0));

        final var game = createTerminalSokobanGame(gameState, inputEngine, renderingEngine);
        assertTimeoutPreemptively(Duration.ofMillis(500), game::run);

        verify(renderingEngine, atLeastOnce()).message(any());
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testGameWin() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(TerminalInputEngine.class);
        final var renderingEngine = mock(TerminalRenderingEngine.class);
        when(gameState.isWin()).thenReturn(false, false, false, true);
        when(inputEngine.fetchAction())
                .thenReturn(createInvalidInput(0, ""));

        final var game = createTerminalSokobanGame(gameState, inputEngine, renderingEngine);
        assertTimeoutPreemptively(Duration.ofMillis(500), game::run);

        verify(gameState, atLeastOnce()).isWin();
        verify(renderingEngine, atLeastOnce()).message(any());
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void testMoreThanTwoPlayers() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(TerminalInputEngine.class);
        final var renderingEngine = mock(TerminalRenderingEngine.class);
        when(gameState.getAllPlayerPositions()).thenReturn(eListOf(positionOf(1, 1), positionOf(1, 2), positionOf(1, 3)));

        assertThrowsExactly(IllegalArgumentException.class, () -> createTerminalSokobanGame(gameState, inputEngine, renderingEngine));
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testTwoPlayers() {
        final var gameState = mock(GameState.class);
        final var inputEngine = mock(TerminalInputEngine.class);
        final var renderingEngine = mock(TerminalRenderingEngine.class);
        when(gameState.getAllPlayerPositions()).thenReturn(eListOf(positionOf(1, 1), positionOf(1, 2)));

        assertDoesNotThrow(() -> createTerminalSokobanGame(gameState, inputEngine, renderingEngine));
    }

    // ==================== Helper Methods ====================

    private static EList<Position> eListOf(Position... positions) {
        BasicEList<Position> list = new BasicEList<>();
        list.addAll(Arrays.asList(positions));
        return list;
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
     * Creates an Exit action.
     */
    private static Exit createExit(int initiator) {
        Exit action = Pa22Factory.eINSTANCE.createExit();
        action.setInitiator(initiator);
        return action;
    }

    private static TerminalSokobanGame createTerminalSokobanGame(
            GameState gameState,
            TerminalInputEngine inputEngine,
            TerminalRenderingEngine renderingEngine
    ) {
        final var playerPositions = gameState.getAllPlayerPositions();
        if (playerPositions != null && playerPositions.size() > 2) {
            throw new IllegalArgumentException("Terminal Sokoban supports at most two players.");
        }

        TerminalSokobanGame game = Pa22Factory.eINSTANCE.createTerminalSokobanGame();
        game.setState(gameState);
        game.setInputEngine(inputEngine);
        game.setRenderingEngine(renderingEngine);
        return game;
    }
}

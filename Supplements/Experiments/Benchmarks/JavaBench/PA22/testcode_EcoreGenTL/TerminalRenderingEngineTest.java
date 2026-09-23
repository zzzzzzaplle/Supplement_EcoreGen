import edu.pa22.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(TestExtension.class)
class TerminalRenderingEngineTest {

    @Tag(TestKind.PUBLIC)
    @Test
    void testMessage() {
        final var stream = new CapturingStream();
        final var randomString = String.valueOf(this.hashCode());

        final var renderingEngine = createTerminalRenderingEngine(stream);
        renderingEngine.message(randomString);

        assertEquals(randomString + System.lineSeparator(), stream.getContent());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testRender() {
        String testMap = """
                233
                ######
                #A..@#
                #...@###
                #a....@##
                #.a.....#
                #..a.####
                ######
                """;
        final var gameState = createGameState(TestHelper.parseGameMap(testMap));
        final var stream = new CapturingStream();

        final var renderingEngine = createTerminalRenderingEngine(stream);
        renderingEngine.render(gameState);

        final var renderedContent = stream.getContent();
        assertEquals(7, renderedContent.lines().count());
        assertTrue(renderedContent.lines().allMatch(it -> it.length() >= 9 && it.length() <= 10)); // On Windows there may be \n\r
        final var lines = renderedContent.lines().toList();
        assertEquals('#', lines.get(0).charAt(0));
        assertEquals(' ', lines.get(0).charAt(8));
        assertEquals('a', lines.get(3).charAt(1));
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
     * Creates a TerminalRenderingEngine with the given PrintStream.
     */
    private static TerminalRenderingEngine createTerminalRenderingEngine(PrintStream stream) {
        TerminalRenderingEngine engine = Pa22Factory.eINSTANCE.createTerminalRenderingEngine();
        engine.setOutputStream(stream);
        return engine;
    }

    static class CapturingStream extends PrintStream {
        public CapturingStream() {
            super(new ByteArrayOutputStream());
        }

        public String getContent() {
            return ((ByteArrayOutputStream) this.out).toString(StandardCharsets.UTF_8);
        }
    }
}

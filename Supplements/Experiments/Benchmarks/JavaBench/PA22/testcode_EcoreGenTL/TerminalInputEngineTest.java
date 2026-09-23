import edu.pa22.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(TestExtension.class)
class TerminalInputEngineTest {

    @Tag(TestKind.HIDDEN)
    @Test
    void testInvalidInput() {
        final var inputStream = fixValueStream("blah blah");

        final var inputEngine = createTerminalInputEngine(inputStream);
        final var action = inputEngine.fetchAction();

        assertTrue(action instanceof InvalidInput);
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testExit() {
        final var inputStream = fixValueStream("exit");

        final var inputEngine = createTerminalInputEngine(inputStream);
        final var action = inputEngine.fetchAction();

        assertTrue(action instanceof Exit);
    }

    @Tag(TestKind.HIDDEN)
    @ParameterizedTest
    @CsvSource({"U,-1"})
    void testUndo(String input, int playerId) {
        final var inputStreamU = fixValueStream(input.toUpperCase());
        final var inputStreamL = fixValueStream(input.toLowerCase());

        final var inputEngineU = createTerminalInputEngine(inputStreamU);
        final var inputEngineL = createTerminalInputEngine(inputStreamL);
        final var actionU = inputEngineU.fetchAction();
        final var actionL = inputEngineL.fetchAction();

        assertTrue(actionU instanceof Undo || actionL instanceof Undo);
    }

    @Tag(TestKind.HIDDEN)
    @ParameterizedTest
    @CsvSource({
        "W,Up,0",
        "A,Left,0",
        "S,Down,0",
        "D,Right,0",
        "H,Left,1",
        "J,Down,1",
        "K,Up,1",
        "L,Right,1",
    })
    void testMove(String input, String name, int playerId) {
        final var inputStreamU = fixValueStream(input.toUpperCase());
        final var inputStreamL = fixValueStream(input.toLowerCase());

        final var inputEngineU = createTerminalInputEngine(inputStreamU);
        final var inputEngineL = createTerminalInputEngine(inputStreamL);
        final var actionU = inputEngineU.fetchAction();
        final var actionL = inputEngineL.fetchAction();

        assertTrue(actionU.getClass().getSimpleName().equals(name) || actionL.getClass().getSimpleName().equals(name));
        assertTrue(actionU.getInitiator() == playerId || actionL.getInitiator() == playerId);
    }

    // ==================== Helper Methods ====================

    private InputStream fixValueStream(String content) {
        final var bytes = content.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
    }

    /**
     * Creates a TerminalInputEngine with the given input stream.
     * This replaces the original constructor `new TerminalInputEngine(InputStream)`.
     */
    private static TerminalInputEngine createTerminalInputEngine(InputStream inputStream) {
        TerminalInputEngine engine = Pa22Factory.eINSTANCE.createTerminalInputEngine();
        engine.setTerminalScanner(new Scanner(inputStream));
        return engine;
    }
}

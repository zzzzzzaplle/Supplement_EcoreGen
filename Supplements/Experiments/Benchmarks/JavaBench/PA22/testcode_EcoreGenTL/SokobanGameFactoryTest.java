import edu.pa22.GameMap;
import edu.pa22.PA22Helper;
import edu.pa22.Pa22Factory;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TestExtension.class)
class SokobanGameFactoryTest {

    @TempDir
    private Path tempDir;

    @Tag(TestKind.PUBLIC)
    @Test
    void testLoadFromFile() throws IOException {
        final var mapFile = tempDir.resolve("tempMap.map");
        String rectangularMap = """
                233
                ######
                #A..@#
                #...@#
                #....#
                #.a..#
                #..a.#
                ######
                """;
        Files.writeString(mapFile, rectangularMap);
        final var gameMap = loadGameMap(mapFile);
        assertEquals(6, gameMap.getMaxWidth());
        assertEquals(7, gameMap.getMaxHeight());
        assertEquals(233, gameMap.getUndoLimit());
    }

    // ==================== Helper Methods ====================

    /**
     * Loads a GameMap from a file.
     * This replaces the original SokobanGameFactory.loadGameMap(Path).
     */
    private static GameMap loadGameMap(Path mapFile) throws IOException {
        String mapText = Files.readString(mapFile);
        PA22Helper pa22Helper = Pa22Factory.eINSTANCE.createPA22Helper();
        return pa22Helper.parseGameMap(mapText);
    }
}

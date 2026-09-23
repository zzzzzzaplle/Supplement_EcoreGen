import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Factory for creating Sokoban games
 */
public class SokobanGameFactory {

    /**
     * Create a TUI version of the Sokoban game.
     *
     * @param mapFile map file.
     * @return The Sokoban game.
     * @throws IOException if mapFile cannot be load
     */
    public static SokobanGame createTUIGame(String mapFile) throws IOException {
        Path file;
        if (!mapFile.endsWith(".map")) {
            // treat as built-in maps
            final URL resource = SokobanGameFactory.class.getClassLoader().getResource(mapFile + ".map");
            if (resource == null) throw new RuntimeException("No such built-in map: " + mapFile);
            try {
                Path resourcePath = Path.of(resource.toURI());
                String mapContent = Files.readString(resourcePath);
                GameMap gameMap = GameMap.parse(mapContent);
                GameState gameState = new GameState(gameMap);
                return new TerminalSokobanGame(gameState, new TerminalInputEngine(System.in), new TerminalRenderingEngine(System.out));
            } catch (URISyntaxException e) {
                throw new IOException("Failed to resolve resource path", e);
            }
        } else {
            file = Path.of(mapFile);
            String mapContent = Files.readString(file);
            GameMap gameMap = GameMap.parse(mapContent);
            GameState gameState = new GameState(gameMap);
            return new TerminalSokobanGame(gameState, new TerminalInputEngine(System.in), new TerminalRenderingEngine(System.out));
        }
    }
}

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SokobanGameFactory {

    public SokobanGameFactory() {
    }

    public static GameMap loadGameMap(Path mapFile) {
        try {
            String content = Files.readString(mapFile);
            return GameMap.parse(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SokobanGame createTUIGame(Path mapFile) {
        GameMap gameMap = loadGameMap(mapFile);
        return new TerminalSokobanGame(new GameState(gameMap));
    }
}

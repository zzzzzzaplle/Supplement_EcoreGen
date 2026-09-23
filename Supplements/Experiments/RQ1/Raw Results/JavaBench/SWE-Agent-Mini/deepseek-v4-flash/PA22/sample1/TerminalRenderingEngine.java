import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

public class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {
    }

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        Set<Position> destinations = state.getDestinations();
        
        for (int y = 0; y < height; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = Position.of(x, y);
                Entity entity = state.getEntity(pos);
                if (entity instanceof Wall) {
                    line.append('#');
                } else if (entity instanceof Box) {
                    int playerId = ((Box) entity).getPlayerId();
                    line.append((char) ('a' + playerId));
                } else if (entity instanceof Player) {
                    int playerId = ((Player) entity).getId();
                    line.append((char) ('A' + playerId));
                } else if (entity instanceof Empty) {
                    if (destinations.contains(pos)) {
                        line.append('@');
                    } else {
                        line.append('.');
                    }
                } else {
                    // No entity at this position, treat as empty
                    if (destinations.contains(pos)) {
                        line.append('@');
                    } else {
                        line.append('.');
                    }
                }
            }
            outputStream.println(line.toString());
        }
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}

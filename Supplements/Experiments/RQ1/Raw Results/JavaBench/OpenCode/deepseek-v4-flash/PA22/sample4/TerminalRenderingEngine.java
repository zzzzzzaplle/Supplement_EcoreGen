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
        Map<Position, Entity> entities = state.getEntities();
        for (int y = 0; y < height; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = Position.of(x, y);
                Entity entity = entities.get(pos);
                if (entity instanceof Wall) {
                    sb.append('#');
                } else if (entity instanceof Box) {
                    int playerId = ((Box) entity).getPlayerId();
                    sb.append((char) ('a' + playerId));
                } else if (entity instanceof Player) {
                    int playerId = ((Player) entity).getId();
                    sb.append((char) ('A' + playerId));
                } else if (entity instanceof Empty) {
                    if (destinations.contains(pos)) {
                        sb.append('@');
                    } else {
                        sb.append('.');
                    }
                } else {
                    sb.append('.');
                }
            }
            outputStream.println(sb.toString());
        }
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }

    public PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}

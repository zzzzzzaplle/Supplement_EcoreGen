import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

public class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public TerminalRenderingEngine() {
    }

    @Override
    public void render(GameState state) {
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        Set<Position> destinations = state.getDestinations();
        
        for (int y = 0; y < height; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                Entity entity = state.getEntity(pos);
                if (entity == null) {
                    sb.append(' ');
                } else if (entity instanceof Wall) {
                    sb.append('#');
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    char c = (char) ('a' + box.getPlayerId());
                    sb.append(c);
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    char c = (char) ('A' + player.getId());
                    sb.append(c);
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

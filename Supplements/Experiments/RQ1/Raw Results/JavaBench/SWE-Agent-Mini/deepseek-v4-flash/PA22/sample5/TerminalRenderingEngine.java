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
                boolean isDestination = destinations.contains(pos);
                
                if (entity instanceof Wall) {
                    line.append('#');
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    char c = (char) ('A' + player.getId());
                    line.append(c);
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    char c = (char) ('a' + box.getPlayerId());
                    line.append(c);
                } else if (entity instanceof Empty) {
                    if (isDestination) {
                        line.append('@');
                    } else {
                        line.append('.');
                    }
                } else {
                    // No entity or null
                    if (isDestination) {
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

    public PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}

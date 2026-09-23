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
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                Entity entity = entities.get(pos);
                boolean isDest = destinations.contains(pos);
                
                if (entity instanceof Wall) {
                    line.append('#');
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    char boxChar = (char) ('a' + box.getPlayerId());
                    line.append(boxChar);
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    char playerChar = (char) ('A' + player.getId());
                    line.append(playerChar);
                } else if (entity instanceof Empty) {
                    if (isDest) {
                        line.append('@');
                    } else {
                        line.append('.');
                    }
                } else if (entity == null) {
                    // Out of bounds
                    line.append('.');
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

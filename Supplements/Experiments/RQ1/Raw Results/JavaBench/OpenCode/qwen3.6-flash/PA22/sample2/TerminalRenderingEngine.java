import java.io.PrintStream;
import java.util.Set;

/**
 * Terminal-based rendering engine implementation.
 */
public class TerminalRenderingEngine implements RenderingEngine {

    private PrintStream outputStream;

    public TerminalRenderingEngine() {
        this(System.out);
    }

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public void render(GameState state) {
        int maxWidth = state.getMapMaxWidth();
        int maxHeight = state.getMapMaxHeight();

        // Build a map of position to entity for quick lookup
        for (int y = 0; y < maxHeight; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < maxWidth; x++) {
                Position pos = Position.of(x, y);
                Entity entity = state.getEntity(pos);

                if (entity == null) {
                    line.append(' ');
                } else if (entity instanceof Wall) {
                    line.append('#');
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    char playerChar = (char) ('A' + player.getId());
                    // Check if on destination
                    Set<Position> destinations = state.getDestinations();
                    if (destinations.contains(pos)) {
                        line.append('@');
                    } else {
                        line.append(playerChar);
                    }
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    char boxChar = (char) ('a' + box.getPlayerId());
                    Set<Position> destinations = state.getDestinations();
                    if (destinations.contains(pos)) {
                        line.append('@'); // Box on destination
                    } else {
                        line.append(boxChar);
                    }
                } else if (entity instanceof Empty) {
                    Set<Position> destinations = state.getDestinations();
                    if (destinations.contains(pos)) {
                        line.append('@');
                    } else {
                        line.append('.');
                    }
                } else {
                    line.append(' ');
                }
            }
            outputStream.println(line.toString());
        }
    }

    public void message(String content) {
        outputStream.println(content);
    }
}

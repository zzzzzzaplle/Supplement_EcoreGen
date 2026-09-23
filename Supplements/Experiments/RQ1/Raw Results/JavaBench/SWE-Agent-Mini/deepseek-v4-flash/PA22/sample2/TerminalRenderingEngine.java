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
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                Entity entity = state.getEntity(pos);
                char displayChar;
                
                if (entity instanceof Wall) {
                    displayChar = '#';
                } else if (entity instanceof Box) {
                    int playerId = ((Box) entity).getPlayerId();
                    displayChar = (char) ('a' + playerId);
                } else if (entity instanceof Player) {
                    int id = ((Player) entity).getId();
                    displayChar = (char) ('A' + id);
                } else if (entity instanceof Empty) {
                    if (destinations.contains(pos)) {
                        displayChar = '@';
                    } else {
                        displayChar = '.';
                    }
                } else if (entity == null) {
                    // Check if it's a destination
                    if (destinations.contains(pos)) {
                        displayChar = '@';
                    } else {
                        displayChar = ' ';
                    }
                } else {
                    displayChar = '.';
                }
                
                sb.append(displayChar);
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

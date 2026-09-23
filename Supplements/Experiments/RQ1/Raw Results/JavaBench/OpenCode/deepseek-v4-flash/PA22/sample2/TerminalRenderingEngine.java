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

        for (int y = 0; y < height; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                Entity entity = state.getEntity(pos);
                Set<Position> destinations = state.getDestinations();

                if (entity instanceof Wall) {
                    sb.append('#');
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    sb.append((char) ('a' + box.getPlayerId()));
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    sb.append((char) ('A' + player.getId()));
                } else if (entity == null || entity instanceof Empty) {
                    if (destinations.contains(pos)) {
                        sb.append('@');
                    } else {
                        sb.append('.');
                    }
                } else {
                    if (destinations.contains(pos)) {
                        sb.append('@');
                    } else {
                        sb.append('.');
                    }
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

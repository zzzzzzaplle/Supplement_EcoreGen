import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

public class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {
        this.outputStream = System.out;
    }

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        Set<Position> destinations = state.getDestinations();
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = Position.of(x, y);
                Entity e = state.getEntity(pos);
                char c;
                if (e == null) {
                    c = '.';
                } else if (e instanceof Wall) {
                    c = '#';
                } else if (e instanceof Box) {
                    Box b = (Box) e;
                    c = (char) ('a' + b.getPlayerId());
                } else if (e instanceof Player) {
                    Player p = (Player) e;
                    c = (char) ('A' + p.getId());
                } else {
                    // Empty
                    if (destinations.contains(pos)) {
                        c = '@';
                    } else {
                        c = '.';
                    }
                }
                sb.append(c);
            }
            sb.append('\n');
        }
        outputStream.print(sb.toString());
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}

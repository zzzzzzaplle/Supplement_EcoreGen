import java.io.PrintStream;
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
        if (outputStream == null || state == null) return;
        int w = state.getMapMaxWidth();
        int h = state.getMapMaxHeight();
        Set<Position> destinations = state.getDestinations();
        for (int y = 0; y < h; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < w; x++) {
                Position p = Position.of(x, y);
                Entity e = state.getEntity(p);
                char c;
                if (e == null) {
                    c = destinations.contains(p) ? '@' : '.';
                } else if (e instanceof Wall) {
                    c = '#';
                } else if (e instanceof Box) {
                    Box b = (Box) e;
                    c = (char) ('a' + b.getPlayerId());
                } else if (e instanceof Player) {
                    Player pl = (Player) e;
                    c = (char) ('A' + pl.getId());
                } else if (e instanceof Empty) {
                    c = destinations.contains(p) ? '@' : '.';
                } else {
                    c = '.';
                }
                sb.append(c);
            }
            outputStream.println(sb.toString());
        }
    }

    @Override
    public void message(String content) {
        if (outputStream == null) return;
        outputStream.println(content == null ? "" : content);
    }
}

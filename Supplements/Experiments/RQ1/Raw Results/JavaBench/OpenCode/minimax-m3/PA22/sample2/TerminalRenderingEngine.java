import java.io.PrintStream;

public class TerminalRenderingEngine implements RenderingEngine {

    private PrintStream outputStream;

    public TerminalRenderingEngine() {
        this.outputStream = System.out;
    }

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        if (state == null || outputStream == null) return;
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        for (int y = 0; y < height; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                Entity e = state.getEntity(pos);
                sb.append(charFor(e, state));
            }
            outputStream.println(sb.toString());
        }
    }

    private char charFor(Entity e, GameState state) {
        if (e == null) return '.';
        if (e instanceof Wall) return '#';
        if (e instanceof Box) {
            Box b = (Box) e;
            return (char) ('a' + b.getPlayerId());
        }
        if (e instanceof Player) {
            Player p = (Player) e;
            return (char) ('A' + p.getId());
        }
        if (e instanceof Empty) {
            if (state.getDestinations().contains(new Position(0, 0))) {
            }
        }
        return '.';
    }

    @Override
    public void message(String content) {
        if (outputStream != null) {
            outputStream.println(content);
        }
    }

    public PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}

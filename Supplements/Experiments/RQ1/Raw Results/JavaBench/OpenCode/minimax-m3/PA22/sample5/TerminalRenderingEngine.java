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
        if (state == null) return;
        int w = state.getMapMaxWidth();
        int h = state.getMapMaxHeight();
        for (int y = 0; y < h; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < w; x++) {
                Position p = Position.of(x, y);
                Entity e = state.getEntity(p);
                char c = renderEntity(e, p, state);
                sb.append(c);
            }
            outputStream.println(sb.toString());
        }
    }

    private char renderEntity(Entity e, Position p, GameState state) {
        if (e == null) return '.';
        if (e instanceof Wall) return '#';
        if (e instanceof Box) {
            Box b = (Box) e;
            return (char) ('a' + b.getPlayerId());
        }
        if (e instanceof Player) {
            Player pl = (Player) e;
            return (char) ('A' + pl.getId());
        }
        if (e instanceof Empty) {
            if (state.getDestinations().contains(p)) {
                return '@';
            }
            return '.';
        }
        return '.';
    }

    @Override
    public void message(String content) {
        if (content == null) content = "";
        outputStream.println(content);
    }

    public PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}

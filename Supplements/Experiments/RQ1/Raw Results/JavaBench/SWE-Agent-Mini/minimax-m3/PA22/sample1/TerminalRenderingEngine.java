import java.io.PrintStream;

public class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {
    }

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        if (state == null) {
            return;
        }
        int w = state.getMapMaxWidth();
        int h = state.getMapMaxHeight();
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Position p = new Position(x, y);
                Entity e = state.getEntity(p);
                char c = '.';
                if (e instanceof Wall) {
                    c = '#';
                } else if (e instanceof Box) {
                    Box b = (Box) e;
                    c = (char) ('a' + b.getPlayerId());
                } else if (e instanceof Player) {
                    Player pl = (Player) e;
                    c = (char) ('A' + pl.getId());
                } else if (e instanceof Empty) {
                    if (state.getDestinations().contains(p)) {
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

    public PrintStream getOutputStream() {
        return this.outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}

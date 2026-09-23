import java.io.PrintStream;

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
        int w = state.getBoardWidth();
        int h = state.getBoardHeight();
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Position p = new Position(x, y);
                Entity e = state.getEntity(p);
                if (e == null) {
                    sb.append(' ');
                } else if (e instanceof Wall) {
                    sb.append('#');
                } else if (e instanceof Box) {
                    Box b = (Box) e;
                    sb.append((char) ('a' + b.getPlayerId()));
                } else if (e instanceof Player) {
                    Player pl = (Player) e;
                    sb.append((char) ('A' + pl.getId()));
                } else if (e instanceof Empty) {
                    if (state.getDestinations().contains(p)) {
                        sb.append('@');
                    } else {
                        sb.append('.');
                    }
                }
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

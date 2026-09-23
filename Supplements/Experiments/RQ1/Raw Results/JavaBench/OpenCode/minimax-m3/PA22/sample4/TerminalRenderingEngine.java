import java.io.PrintStream;

public class TerminalRenderingEngine implements RenderingEngine {

    private PrintStream outputStream;

    public TerminalRenderingEngine() {
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
        if (outputStream == null || state == null) {
            return;
        }
        int height = state.getMapMaxHeight();
        int width = state.getMapMaxWidth();
        for (int y = 0; y < height; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                Entity entity = state.getEntity(pos);
                char c;
                if (entity instanceof Wall) {
                    c = '#';
                } else if (entity instanceof Box) {
                    int pid = ((Box) entity).getPlayerId();
                    c = (char) ('a' + Math.min(pid, 25));
                } else if (entity instanceof Player) {
                    int pid = ((Player) entity).getId();
                    c = (char) ('A' + Math.min(pid, 25));
                } else {
                    if (state.getDestinations().contains(pos)) {
                        c = '@';
                    } else {
                        c = '.';
                    }
                }
                sb.append(c);
            }
            outputStream.println(sb.toString());
        }
    }

    @Override
    public void message(String content) {
        if (outputStream != null) {
            outputStream.println(content);
        }
    }
}

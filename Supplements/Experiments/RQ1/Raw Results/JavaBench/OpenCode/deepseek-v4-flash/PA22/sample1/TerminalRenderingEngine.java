import java.io.PrintStream;

public class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public TerminalRenderingEngine() {
    }

    @Override
    public void render(GameState state) {
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        for (int y = 0; y < height; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = Position.of(x, y);
                Entity entity = state.getEntity(pos);
                if (entity instanceof Wall) {
                    sb.append('#');
                } else if (entity instanceof Box) {
                    int playerId = ((Box) entity).getPlayerId();
                    sb.append((char) ('a' + playerId));
                } else if (entity instanceof Player) {
                    int id = ((Player) entity).getId();
                    sb.append((char) ('A' + id));
                } else if (entity instanceof Empty) {
                    if (state.getDestinations().contains(pos)) {
                        sb.append('@');
                    } else {
                        sb.append('.');
                    }
                } else {
                    sb.append(' ');
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

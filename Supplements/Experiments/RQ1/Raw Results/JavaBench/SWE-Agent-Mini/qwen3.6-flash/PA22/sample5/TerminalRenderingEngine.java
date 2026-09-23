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
        int height = state.getMapMaxHeight();
        int width = state.getMapMaxWidth();

        for (int y = 0; y < height; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < width; x++) {
                Position pos = Position.of(x, y);
                Entity entity = state.getEntity(pos);
                if (entity == null) {
                    line.append(' ');
                } else if (entity instanceof Wall) {
                    line.append('#');
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    line.append((char) ('A' + player.getId()));
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    line.append((char) ('a' + box.getPlayerId()));
                } else if (entity instanceof Empty) {
                    if (state.getDestinations().contains(pos)) {
                        line.append('@');
                    } else {
                        line.append('.');
                    }
                } else {
                    line.append(' ');
                }
            }
            outputStream.println(line.toString());
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

import java.io.PrintStream;

public class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {
    }

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public void render(GameState state) {
        for (int y = 0; y < state.getMapMaxHeight(); y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < state.getMapMaxWidth(); x++) {
                Position pos = Position.of(x, y);
                Entity entity = state.getEntity(pos);
                if (entity instanceof Wall) {
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
                    line.append('.');
                }
            }
            outputStream.println(line.toString());
        }
    }

    public void message(String content) {
        outputStream.println(content);
    }
}

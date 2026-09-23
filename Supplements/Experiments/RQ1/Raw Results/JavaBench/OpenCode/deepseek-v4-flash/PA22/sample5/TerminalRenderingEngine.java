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
        for (int y = 0; y < state.getMapMaxHeight(); y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < state.getMapMaxWidth(); x++) {
                Position pos = Position.of(x, y);
                Entity entity = state.getEntity(pos);
                if (entity instanceof Wall) {
                    sb.append('#');
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    sb.append((char) ('a' + box.getPlayerId()));
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    sb.append((char) ('A' + player.getId()));
                } else if (entity instanceof Empty) {
                    if (state.getDestinations().contains(pos)) {
                        sb.append('@');
                    } else {
                        sb.append('.');
                    }
                }
            }
            outputStream.println(sb.toString());
        }
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}

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
        int width = state.getMapMaxWidth();
        int height = state.getMapMaxHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                Entity entity = state.getEntity(pos);
                char c;
                if (entity instanceof Wall) {
                    c = '#';
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    c = (char) ('a' + box.getPlayerId());
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    c = (char) ('A' + player.getId());
                } else {
                    if (state.getDestinations().contains(pos)) {
                        c = '@';
                    } else {
                        c = '.';
                    }
                }
                outputStream.print(c);
            }
            outputStream.println();
        }
    }

    @Override
    public void message(String content) {
        if (outputStream != null) {
            outputStream.println(content);
        }
    }
}

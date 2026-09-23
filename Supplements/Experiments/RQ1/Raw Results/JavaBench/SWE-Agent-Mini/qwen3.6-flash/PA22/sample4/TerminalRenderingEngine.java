import java.io.PrintStream;

class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public TerminalRenderingEngine() {
    }

    public void render(GameState state) {
        int maxWidth = state.getMapMaxWidth();
        int maxHeight = state.getMapMaxHeight();
        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < maxWidth; x++) {
                Position pos = new Position(x, y);
                Entity entity = state.getEntity(pos);
                if (entity == null) {
                    if (state.getDestinations().contains(pos)) {
                        outputStream.print('@');
                    } else {
                        outputStream.print('.');
                    }
                } else if (entity instanceof Wall) {
                    outputStream.print('#');
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    outputStream.print((char) ('a' + box.getPlayerId()));
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    outputStream.print((char) ('A' + player.getId()));
                } else if (entity instanceof Empty) {
                    outputStream.print(' ');
                }
            }
            outputStream.println();
        }
    }

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

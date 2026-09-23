import java.io.PrintStream;

public class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public void render(GameState state) {
        int maxWidth = state.getMapMaxWidth();
        int maxHeight = state.getMapMaxHeight();
        
        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < maxWidth; x++) {
                Position pos = Position.of(x, y);
                Entity entity = state.getEntity(pos);
                char displayChar = renderEntity(entity, pos, state);
                outputStream.print(displayChar);
            }
            outputStream.println();
        }
    }
    
    private char renderEntity(Entity entity, Position pos, GameState state) {
        if (entity instanceof Wall) {
            return '#';
        } else if (entity instanceof Player) {
            Player player = (Player) entity;
            return (char) ('A' + player.getId());
        } else if (entity instanceof Box) {
            Box box = (Box) entity;
            return (char) ('a' + box.getPlayerId());
        } else if (entity instanceof Empty) {
            if (state.getDestinations().contains(pos)) {
                return '@';
            }
            return '.';
        }
        return ' ';
    }

    public void message(String content) {
        outputStream.println(content);
    }
}

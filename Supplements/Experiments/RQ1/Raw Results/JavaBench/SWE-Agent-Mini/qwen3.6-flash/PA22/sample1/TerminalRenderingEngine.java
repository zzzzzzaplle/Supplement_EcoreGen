import java.io.PrintStream;
import java.util.Optional;

public class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {
    }

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        int maxWidth = state.getMapMaxWidth();
        int maxHeight = state.getMapMaxHeight();
        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < maxWidth; x++) {
                Position pos = Position.of(x, y);
                Entity entity = state.getEntity(pos);
                outputStream.print(renderEntity(entity, pos, state));
            }
            outputStream.println();
        }
        Optional<Integer> undoLimit = state.getUndoLimitOptional();
        if (undoLimit.isPresent()) {
            if (undoLimit.get() > 0) {
                outputStream.println(String.format(StringResources.UNDO_QUOTA_TEMPLATE, String.valueOf(undoLimit.get())));
            } else {
                outputStream.println(StringResources.UNDO_QUOTA_UNLIMITED);
            }
        }
    }

    private String renderEntity(Entity entity, Position pos, GameState state) {
        if (!(entity instanceof Empty)) {
            if (entity instanceof Wall) {
                return "#";
            } else if (entity instanceof Player) {
                Player player = (Player) entity;
                return String.valueOf((char)('A' + player.getId()));
            } else if (entity instanceof Box) {
                Box box = (Box) entity;
                return String.valueOf((char)('a' + box.getPlayerId()));
            }
        }
        if (state.getDestinations().contains(pos)) {
            return "@";
        }
        return ".";
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}

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
        int w = state.getMapMaxWidth();
        int h = state.getMapMaxHeight();
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Position p = new Position(x, y);
                Entity e = state.getEntity(p);
                char c = renderChar(state, p, e);
                sb.append(c);
            }
            sb.append('\n');
        }
        outputStream.print(sb.toString());
        if (state.getUndoQuota() == -1) {
            outputStream.println(String.format(StringResources.UNDO_QUOTA_TEMPLATE, StringResources.UNDO_QUOTA_UNLIMITED));
        } else {
            outputStream.println(String.format(StringResources.UNDO_QUOTA_TEMPLATE, state.getUndoQuota()));
        }
    }

    private char renderChar(GameState state, Position pos, Entity e) {
        boolean isDest = state.getDestinations().contains(pos);
        if (e instanceof Wall) {
            return '#';
        }
        if (e instanceof Player) {
            Player p = (Player) e;
            return (char) ('A' + p.getId());
        }
        if (e instanceof Box) {
            Box b = (Box) e;
            return (char) ('a' + b.getPlayerId());
        }
        // empty
        if (isDest) {
            return '@';
        }
        return '.';
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}

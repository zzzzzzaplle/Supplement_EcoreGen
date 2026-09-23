import java.io.PrintStream;
import java.util.*;

class TerminalRenderingEngine implements RenderingEngine {

    private PrintStream outputStream;

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        int maxWidth = state.getMapMaxWidth();
        int maxHeight = state.getMapMaxHeight();
        Set<Position> destinations = state.getDestinations();
        Map<Position, Entity> board = state.getBoard();

        for (int y = 0; y < maxHeight; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x < maxWidth; x++) {
                Position pos = Position.of(x, y);
                Entity entity = board.get(pos);
                char displayChar;

                if (entity instanceof Wall) {
                    displayChar = '#';
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    displayChar = (char) ('a' + box.getPlayerId());
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    displayChar = (char) ('A' + player.getId());
                } else if (entity instanceof Empty) {
                    if (destinations.contains(pos)) {
                        displayChar = '@';
                    } else {
                        displayChar = '.';
                    }
                } else {
                    if (destinations.contains(pos)) {
                        displayChar = '@';
                    } else {
                        displayChar = ' ';
                    }
                }
                line.append(displayChar);
            }
            this.outputStream.println(line.toString());
        }
    }

    @Override
    public void message(String content) {
        this.outputStream.println(content);
    }
}

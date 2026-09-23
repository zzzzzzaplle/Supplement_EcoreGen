
import java.io.*;
import java.util.*;

/**
 * Terminal rendering engine that outputs to a PrintStream.
 */
public class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        for (int y = 0; y < state.getMapMaxHeight(); y++) {
            for (int x = 0; x < state.getMapMaxWidth(); x++) {
                Position pos = Position.of(x, y);
                Entity entity = state.getEntity(pos);
                if (entity == null) {
                    // This shouldn't happen - the map should have all positions filled
                    if (state.getDestinations().contains(pos)) {
                        outputStream.print('@');
                    } else {
                        outputStream.print('.');
                    }
                } else if (entity instanceof Player) {
                    Player player = (Player) entity;
                    char c = (char) ('A' + player.getId());
                    outputStream.print(c);
                } else if (entity instanceof Box) {
                    Box box = (Box) entity;
                    char c = (char) ('a' + box.getPlayerId());
                    outputStream.print(c);
                } else if (entity instanceof Wall) {
                    outputStream.print('#');
                } else if (entity instanceof Empty) {
                    if (state.getDestinations().contains(pos)) {
                        outputStream.print('@');
                    } else {
                        outputStream.print('.');
                    }
                } else {
                    if (state.getDestinations().contains(pos)) {
                        outputStream.print('@');
                    } else {
                        outputStream.print('.');
                    }
                }
            }
            outputStream.println();
        }
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}
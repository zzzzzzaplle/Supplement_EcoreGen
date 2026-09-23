import java.io.BufferedReader;
import java.io.BufferedWriter;

public class GameStateSerializer {
    public GameStateSerializer() {
    }

    public static void writeTo(GameState state, BufferedWriter writer) {
    }

    public static GameState loadFrom(BufferedReader reader) {
        return new GameState();
    }
}

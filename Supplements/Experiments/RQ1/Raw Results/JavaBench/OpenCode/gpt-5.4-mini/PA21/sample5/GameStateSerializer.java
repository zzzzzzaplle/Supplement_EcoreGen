public class GameStateSerializer {

    public GameStateSerializer() {
    }

    public static java.nio.file.Path writeTo(final GameState gameState, final java.nio.file.Path outputFile)
            throws java.nio.file.FileAlreadyExistsException {
        throw new UnsupportedOperationException();
    }

    static void writeTo(final GameState gameState, final java.io.BufferedWriter writer)
            throws java.io.IOException {
        throw new UnsupportedOperationException();
    }

    public static GameState loadFrom(final java.nio.file.Path inputFile)
            throws java.io.FileNotFoundException {
        throw new UnsupportedOperationException();
    }

    static GameState loadFrom(final java.io.BufferedReader reader) throws java.io.IOException {
        throw new UnsupportedOperationException();
    }
}

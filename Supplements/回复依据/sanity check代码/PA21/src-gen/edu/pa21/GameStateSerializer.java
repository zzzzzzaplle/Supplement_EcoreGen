package edu.pa21;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public final class GameStateSerializer {
    private GameStateSerializer() {
        throw new UnsupportedOperationException();
    }

    public static void writeTo(GameState gameState, BufferedWriter writer) throws IOException {
        throw new UnsupportedOperationException();
    }

    public static GameState loadFrom(BufferedReader reader) throws IOException {
        throw new UnsupportedOperationException();
    }

    public static GameState loadFrom(Path path) throws FileNotFoundException {
        throw new UnsupportedOperationException();
    }
}

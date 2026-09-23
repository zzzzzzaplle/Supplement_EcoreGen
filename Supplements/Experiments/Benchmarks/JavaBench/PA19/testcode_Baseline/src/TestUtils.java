import java.util.List;

/** Helpers for unit tests. */
public final class TestUtils {
    private TestUtils() {
    }

    public static Pipe pipe(PipeShape shape) {
        Pipe p = new Pipe();
        p.setShape(shape);
        return p;
    }

    public static Pipe pipeFromString(String rep) {
        return Pipe.fromString(rep);
    }

    public static Game gameFromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        return Game.fromString(rows, cols, delay, cellsRep, pipes);
    }
}

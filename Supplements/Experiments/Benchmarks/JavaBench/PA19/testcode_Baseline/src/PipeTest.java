import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PipeTest {

    private static Pipe createPipe(PipeShape shape) {
        Pipe p = new Pipe();
        p.setShape(shape);
        return p;
    }

    @Test
    void connection() {
        Pipe pipe;
        pipe = createPipe(PipeShape.HORIZONTAL);
        assertArrayEquals(new Direction[]{Direction.LEFT, Direction.RIGHT}, pipe.getConnections());

        pipe = createPipe(PipeShape.TOP_LEFT);
        assertArrayEquals(new Direction[]{Direction.UP, Direction.LEFT}, pipe.getConnections());

        pipe = createPipe(PipeShape.BOTTOM_LEFT);
        assertArrayEquals(new Direction[]{Direction.DOWN, Direction.LEFT}, pipe.getConnections());

        pipe = createPipe(PipeShape.CROSS);
        assertArrayEquals(Direction.values(), pipe.getConnections());
    }
}

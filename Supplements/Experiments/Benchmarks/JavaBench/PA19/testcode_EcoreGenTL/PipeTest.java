import edu.pa19.Direction;
import edu.pa19.Pa19Factory;
import edu.pa19.Pipe;
import edu.pa19.PipeShape;
import org.eclipse.emf.common.util.EList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PipeTest {

    @Test
    void connection() {
        Pipe pipe;

        pipe = createPipe(PipeShape.HORIZONTAL);
        assertConnections(pipe.getConnections(), Direction.LEFT, Direction.RIGHT);

        pipe = createPipe(PipeShape.TOP_LEFT);
        assertConnections(pipe.getConnections(), Direction.UP, Direction.LEFT);

        pipe = createPipe(PipeShape.BOTTOM_LEFT);
        assertConnections(pipe.getConnections(), Direction.DOWN, Direction.LEFT);

        pipe = createPipe(PipeShape.CROSS);
        assertConnections(pipe.getConnections(), Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
    }

    private static Pipe createPipe(PipeShape shape) {
        Pipe p = Pa19Factory.eINSTANCE.createPipe();
        p.setShape(shape);
        p.setFilled(false);
        return p;
    }

    private static void assertConnections(EList<Direction> actual, Direction... expected) {
        assertEquals(expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual.get(i));
        }
    }
}

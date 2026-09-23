import edu.pa19.Coordinate;
import edu.pa19.FillableCell;
import edu.pa19.Pa19Factory;
import edu.pa19.Pipe;
import edu.pa19.PipeShape;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FillableCellTest {

    private static final Coordinate DEFAULT_COORD = Pa19FactoryHelper.createCoordinate(1, 2);
    private static final Pipe DEFAULT_PIPE = createPipe(PipeShape.CROSS);

    private FillableCell cell = null;

    @Test
    void givenCell_assertCorrectCoordinates() {
        cell = Pa19Factory.eINSTANCE.createFillableCell();
        cell.setCoord(DEFAULT_COORD);

        assertEquals(DEFAULT_COORD, cell.getCoord());
        assertNull(cell.getPipe());
    }

    @Test
    void givenFilledCell_assertCorrectCoordinatesAndPipe() {
        cell = Pa19Factory.eINSTANCE.createFillableCell();
        cell.setCoord(DEFAULT_COORD);
        cell.setPipe(DEFAULT_PIPE);

        assertEquals(DEFAULT_COORD, cell.getCoord());
        assertEquals(DEFAULT_PIPE, cell.getPipe());
    }

    @Test
    void givenCell_assertSingleCharRepresentation() {
        cell = Pa19Factory.eINSTANCE.createFillableCell();
        cell.setCoord(DEFAULT_COORD);

        assertEquals('.', cell.toSingleChar());
    }

    @Test
    void givenFilledCell_assertSingleCharRepresentation() {
        cell = Pa19Factory.eINSTANCE.createFillableCell();
        cell.setCoord(DEFAULT_COORD);
        cell.setPipe(DEFAULT_PIPE);

        assertEquals(DEFAULT_PIPE.toSingleChar(), cell.toSingleChar());
    }

    @AfterEach
    void tearDown() {
        cell = null;
    }

    private static Pipe createPipe(PipeShape shape) {
        Pipe pipe = Pa19Factory.eINSTANCE.createPipe();
        pipe.setShape(shape);
        pipe.setFilled(false);
        return pipe;
    }
}

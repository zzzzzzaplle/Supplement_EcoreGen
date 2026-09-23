import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FillableCellTest {

    private static Coordinate DEFAULT_COORD = new Coordinate(1, 2);
    private static Pipe DEFAULT_PIPE;
    static {
        DEFAULT_PIPE = new Pipe();
        DEFAULT_PIPE.setShape(PipeShape.CROSS);
    }

    private FillableCell cell = null;

    private static FillableCell createFillableCell(Coordinate coord) {
        FillableCell cell = new FillableCell();
        cell.setCoord(coord);
        return cell;
    }

    private static FillableCell createFillableCell(Coordinate coord, Pipe pipe) {
        FillableCell cell = new FillableCell();
        cell.setCoord(coord);
        cell.setPipe(pipe);
        return cell;
    }

    @Test
    void givenCell_assertCorrectCoordinates() {
        cell = createFillableCell(DEFAULT_COORD);

        assertEquals(DEFAULT_COORD, cell.coord);
        assertTrue(cell.getPipe().isEmpty());
    }

    @Test
    void givenFilledCell_assertCorrectCoordinatesAndPipe() {
        cell = createFillableCell(DEFAULT_COORD, DEFAULT_PIPE);

        assertEquals(DEFAULT_COORD, cell.coord);
        assertEquals(DEFAULT_PIPE, cell.getPipe().orElse(null));
    }

    @Test
    void givenCell_assertSingleCharRepresentation() {
        cell = createFillableCell(DEFAULT_COORD);

        assertEquals('.', cell.toSingleChar());
    }

    @Test
    void givenFilledCell_assertSingleCharRepresentation() {
        cell = createFillableCell(DEFAULT_COORD, DEFAULT_PIPE);

        assertEquals(DEFAULT_PIPE.toSingleChar(), cell.toSingleChar());
    }

    @AfterEach
    void tearDown() {
        cell = null;
    }
}

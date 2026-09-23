import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    private static TerminationType createTerminationTypeSource() {
        return TerminationType.SOURCE;
    }

    private static Coordinate DEFAULT_COORDINATE = new Coordinate(1, 2);

    private Cell cell = null;

    @Test
    void givenUnknownCHar_whenCreateCellFromChar_assertNull() {
        cell = Cell.fromChar('A', DEFAULT_COORDINATE, null);

        assertNull(cell);
    }

    @Test
    void givenWallChar_whenCreateCellFromChar_assertCorrectType() {
        cell = Cell.fromChar('W', DEFAULT_COORDINATE, null);

        assertTrue(cell instanceof Wall);
        assertEquals(DEFAULT_COORDINATE, cell.getCoord());
    }

    @Test
    void givenWallChar_whenCreateCellFromCharWithTerminationType_assertCorrectType() {
        cell = Cell.fromChar('W', DEFAULT_COORDINATE, createTerminationTypeSource());

        assertTrue(cell instanceof Wall);
        assertEquals(DEFAULT_COORDINATE, cell.getCoord());
    }

    @Test
    void givenCellChar_whenCreateCellFromChar_assertCorrectType() {
        cell = Cell.fromChar('.', DEFAULT_COORDINATE, null);

        assertTrue(cell instanceof FillableCell);
        assertEquals(DEFAULT_COORDINATE, cell.getCoord());
    }

    @Test
    void givenCellChar_whenCreateCellFromCharWithTerminationType_assertCorrectType() {
        cell = Cell.fromChar('.', DEFAULT_COORDINATE, createTerminationTypeSource());

        assertTrue(cell instanceof FillableCell);
        assertEquals(DEFAULT_COORDINATE, cell.getCoord());
    }

    @Test
    void givenTerminationCellChar_whenCreateCellFromCharWithParams_assertCorrectType() {
        cell = Cell.fromChar('^', DEFAULT_COORDINATE, createTerminationTypeSource());

        assertTrue(cell instanceof TerminationCell);
        assertEquals(DEFAULT_COORDINATE, cell.getCoord());
        assertEquals(Direction.UP, ((TerminationCell) cell).getPointingTo());
        assertEquals(createTerminationTypeSource(), ((TerminationCell) cell).getType());
    }

    @Test
    void givenTerminationCellChar_whenCreateCellFromCharWithoutParams_assertNull() {
        cell = Cell.fromChar('^', DEFAULT_COORDINATE, null);

        assertNull(cell);
    }

    @AfterEach
    void tearDown() {
        cell = null;
    }
}

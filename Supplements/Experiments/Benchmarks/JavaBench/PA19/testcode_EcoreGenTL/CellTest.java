import edu.pa19.Cell;
import edu.pa19.Coordinate;
import edu.pa19.Direction;
import edu.pa19.FillableCell;
import edu.pa19.Pa19Helper;
import edu.pa19.TerminationCell;
import edu.pa19.TerminationType;
import edu.pa19.Wall;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    private static final Coordinate DEFAULT_COORDINATE = Pa19FactoryHelper.createCoordinate(1, 2);

    private Cell cell = null;

    @Test
    void givenUnknownCHar_whenCreateCellFromChar_assertNull() {
        cell = Pa19FactoryHelper.cellFromChar('A', DEFAULT_COORDINATE, null);

        assertNull(cell);
    }

    @Test
    void givenWallChar_whenCreateCellFromChar_assertCorrectType() {
        cell = Pa19FactoryHelper.cellFromChar('W', DEFAULT_COORDINATE, null);

        assertTrue(cell instanceof Wall);
        assertEquals(DEFAULT_COORDINATE, cell.getCoord());
    }

    @Test
    void givenWallChar_whenCreateCellFromCharWithTerminationType_assertCorrectType() {
        cell = Pa19FactoryHelper.cellFromChar('W', DEFAULT_COORDINATE, TerminationType.SOURCE);

        assertTrue(cell instanceof Wall);
        assertEquals(DEFAULT_COORDINATE, cell.getCoord());
    }

    @Test
    void givenCellChar_whenCreateCellFromChar_assertCorrectType() {
        cell = Pa19FactoryHelper.cellFromChar('.', DEFAULT_COORDINATE, null);

        assertTrue(cell instanceof FillableCell);
        assertEquals(DEFAULT_COORDINATE, cell.getCoord());
    }

    @Test
    void givenCellChar_whenCreateCellFromCharWithTerminationType_assertCorrectType() {
        cell = Pa19FactoryHelper.cellFromChar('.', DEFAULT_COORDINATE, TerminationType.SOURCE);

        assertTrue(cell instanceof FillableCell);
        assertEquals(DEFAULT_COORDINATE, cell.getCoord());
    }

    @Test
    void givenTerminationCellChar_whenCreateCellFromCharWithParams_assertCorrectType() {
        cell = Pa19FactoryHelper.cellFromChar('^', DEFAULT_COORDINATE, TerminationType.SOURCE);

        assertTrue(cell instanceof TerminationCell);
        TerminationCell terminationCell = (TerminationCell) cell;
        assertEquals(DEFAULT_COORDINATE, terminationCell.getCoord());
        assertEquals(Direction.UP, terminationCell.getPointingTo());
        assertEquals(TerminationType.SOURCE, terminationCell.getType());
    }

    @Test
    void givenTerminationCellChar_whenCreateCellFromCharWithoutParams_assertNull() {
        cell = Pa19FactoryHelper.cellFromChar('^', DEFAULT_COORDINATE, null);

        assertNull(cell);
    }

    @AfterEach
    void tearDown() {
        cell = null;
    }
}

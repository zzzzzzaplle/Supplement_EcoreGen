import edu.pa19.Coordinate;
import edu.pa19.Direction;
import edu.pa19.Pa19Factory;
import edu.pa19.TerminationCell;
import edu.pa19.TerminationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TerminationCellTest {

    private static final Coordinate DEFAULT_COORD = Pa19FactoryHelper.createCoordinate(1, 2);
    private static final Direction DEFAULT_DIR = Direction.UP;
    private static final TerminationType DEFAULT_TYPE = TerminationType.SOURCE;

    private TerminationCell cell = null;

    @Test
    void givenCell_assertCorrectProperties() {
        cell = Pa19Factory.eINSTANCE.createTerminationCell();
        cell.setCoord(DEFAULT_COORD);
        cell.setPointingTo(DEFAULT_DIR);
        cell.setType(DEFAULT_TYPE);

        assertEquals(DEFAULT_COORD, cell.getCoord());
        assertEquals(DEFAULT_DIR, cell.getPointingTo());
        assertEquals(DEFAULT_TYPE, cell.getType());
    }

    @Test
    void givenCell_assertSingleCharRepresentation() {
        cell = Pa19Factory.eINSTANCE.createTerminationCell();
        cell.setCoord(DEFAULT_COORD);
        cell.setPointingTo(DEFAULT_DIR);
        cell.setType(DEFAULT_TYPE);

        assertEquals('△', cell.toSingleChar());
    }

    @Test
    void givenFilledCell_assertSingleCharRepresentation() {
        cell = Pa19Factory.eINSTANCE.createTerminationCell();
        cell.setCoord(DEFAULT_COORD);
        cell.setPointingTo(DEFAULT_DIR);
        cell.setType(DEFAULT_TYPE);
        cell.setFilled(true);

        assertEquals('▲', cell.toSingleChar());
    }

    @AfterEach
    void tearDown() {
        cell = null;
    }
}

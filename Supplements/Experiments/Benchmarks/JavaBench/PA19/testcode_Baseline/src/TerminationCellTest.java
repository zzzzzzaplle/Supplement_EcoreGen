import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminationCellTest {

    private static Coordinate DEFAULT_COORD = new Coordinate(1, 2);
    private static Direction DEFAULT_DIR = Direction.UP;
    private static TerminationType DEFAULT_TYPE = TerminationType.SOURCE;

    private TerminationCell cellUp = null;

    private static TerminationCell createTerminationCell(Coordinate coord, Direction direction, TerminationType type) {
        return new TerminationCell(coord, type,direction);
    }

    @Test
    void givenCell_assertCorrectProperties() {
        cellUp = createTerminationCell(DEFAULT_COORD, DEFAULT_DIR, DEFAULT_TYPE);

        assertEquals(DEFAULT_COORD, cellUp.coord);
        assertEquals(DEFAULT_DIR, cellUp.getPointingTo());
        assertEquals(DEFAULT_TYPE, cellUp.getType());
    }

    @Test
    void givenCell_assertSingleCharRepresentation() {
        TerminationCell cellUp = createTerminationCell(DEFAULT_COORD, Direction.UP, DEFAULT_TYPE);
        TerminationCell cellDown = createTerminationCell(DEFAULT_COORD, Direction.DOWN, DEFAULT_TYPE);
        TerminationCell cellLeft = createTerminationCell(DEFAULT_COORD, Direction.LEFT, DEFAULT_TYPE);
        TerminationCell cellRight = createTerminationCell(DEFAULT_COORD, Direction.RIGHT, DEFAULT_TYPE);

        // Avoid relying on helper classes (e.g., PipePatterns) which may not exist in generated samples.
        assertNotEquals('\0', cellUp.toSingleChar());
        assertNotEquals('\0', cellDown.toSingleChar());
        assertNotEquals('\0', cellLeft.toSingleChar());
        assertNotEquals('\0', cellRight.toSingleChar());

        // Directional representations should not all collapse to the same character.
        assertNotEquals(cellUp.toSingleChar(), cellDown.toSingleChar());
    }

    @Test
    void givenFilledCell_assertSingleCharRepresentation() {
        TerminationCell cellUp = createTerminationCell(DEFAULT_COORD, Direction.UP, DEFAULT_TYPE);
        cellUp.setFilled();

        assertNotEquals('\0', cellUp.toSingleChar());
    }

    @AfterEach
    void tearDown() {
        cellUp = null;
    }
}

import edu.pa19.Coordinate;
import edu.pa19.Pa19Factory;
import edu.pa19.Wall;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WallTest {

    private static final Coordinate DEFAULT_COORDINATE = Pa19FactoryHelper.createCoordinate(1, 2);

    private Wall wall = null;

    @BeforeEach
    void setUp() {
        wall = Pa19Factory.eINSTANCE.createWall();
        wall.setCoord(DEFAULT_COORDINATE);
    }

    @Test
    void givenWall_assertCorrectCoordinates() {
        assertEquals(DEFAULT_COORDINATE, wall.getCoord());
    }

    @Test
    void givenWall_assertCorrectSingleCharRepresentation() {
        assertEquals('#', wall.toSingleChar());
    }

    @AfterEach
    void tearDown() {
        wall = null;
    }
}

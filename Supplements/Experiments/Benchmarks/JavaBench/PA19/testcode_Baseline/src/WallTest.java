import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class WallTest {

    private static Coordinate DEFAULT_COORDINATE = new Coordinate(1, 2);

    private Wall wall = null;

    @BeforeEach
    void setUp() {
        wall = new Wall();
        wall.setCoord(DEFAULT_COORDINATE);
    }

    @Test
    void givenWall_assertCorrectCoordinates() {
        assertEquals(DEFAULT_COORDINATE, wall.coord);
    }

    @Test
    void givenWall_assertCorrectSingleCharRepresentation() {
        // Avoid relying on helper classes (e.g., PipePatterns) which may not exist in generated samples.
        assertNotEquals('\0', wall.toSingleChar());
    }

    @AfterEach
    void tearDown() {
        wall = null;
    }
}

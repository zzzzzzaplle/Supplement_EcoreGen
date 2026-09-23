import edu.pa19.Coordinate;
import edu.pa19.Direction;
import edu.pa19.Pa19Factory;
import edu.pa19.Pa19Helper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectionTest {

    private static final Pa19Helper HELPER = Pa19Factory.eINSTANCE.createPa19Helper();

    private static final Coordinate UP_OFFSET = Pa19FactoryHelper.createCoordinate(-1, 0);
    private static final Coordinate DOWN_OFFSET = Pa19FactoryHelper.createCoordinate(1, 0);
    private static final Coordinate LEFT_OFFSET = Pa19FactoryHelper.createCoordinate(0, -1);
    private static final Coordinate RIGHT_OFFSET = Pa19FactoryHelper.createCoordinate(0, 1);

    @Test
    void givenDirections_ifCorrectOppositeDirection_thenSucceed() {
        assertEquals(Direction.DOWN, HELPER.getOpposite(Direction.UP));
        assertEquals(Direction.UP, HELPER.getOpposite(Direction.DOWN));
        assertEquals(Direction.RIGHT, HELPER.getOpposite(Direction.LEFT));
        assertEquals(Direction.LEFT, HELPER.getOpposite(Direction.RIGHT));
    }

    @Test
    void givenDirections_ifCorrectCoordOffset_thenSucceed() {
        assertCoordinateEquals(UP_OFFSET, HELPER.getOffset(Direction.UP));
        assertCoordinateEquals(DOWN_OFFSET, HELPER.getOffset(Direction.DOWN));
        assertCoordinateEquals(LEFT_OFFSET, HELPER.getOffset(Direction.LEFT));
        assertCoordinateEquals(RIGHT_OFFSET, HELPER.getOffset(Direction.RIGHT));
    }

    private static void assertCoordinateEquals(Coordinate expected, Coordinate actual) {
        assertEquals(expected.getRow(), actual.getRow());
        assertEquals(expected.getCol(), actual.getCol());
    }
}

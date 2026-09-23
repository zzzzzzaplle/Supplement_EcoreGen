import edu.pa19.Coordinate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CoordinateTest {

    private Coordinate coord = null;

    @BeforeEach
    void setUp() {
        coord = Pa19FactoryHelper.createCoordinate(1, 2);
    }

    @Test
    void givenCoord_ifRowAndColMatches_thenSucceed() {
        assertEquals(1, coord.getRow());
        assertEquals(2, coord.getCol());
    }

    @Test
    void givenCoord_whenCompareSameCoords_assertEquals() {
        final Coordinate expected = Pa19FactoryHelper.createCoordinate(1, 2);

        assertTrue(coord.equals(expected));
    }

    @Test
    void givenCoord_whenCompareSameCoordsAsObject_assertEquals() {
        final Coordinate expected = Pa19FactoryHelper.createCoordinate(1, 2);

        assertEquals(coord, expected);
    }

    @Test
    void givenCoord_whenCompareCoordsWithDifferentRow_assertNotEquals() {
        final Coordinate unexpected = Pa19FactoryHelper.createCoordinate(2, 2);

        assertNotEquals(unexpected, coord);
    }

    @Test
    void givenCoord_whenCompareCoordsWithDifferentCol_assertNotEquals() {
        final Coordinate unexpected = Pa19FactoryHelper.createCoordinate(1, 1);

        assertNotEquals(unexpected, coord);
    }

    @Test
    void givenCoord_whenCompareCoordsWithDifferentRowAndCol_assertNotEquals() {
        final Coordinate unexpected = Pa19FactoryHelper.createCoordinate(2, 1);

        assertNotEquals(unexpected, coord);
    }

    @Test
    void givenCoord_whenCompareWithNull_assertNotEquals() {
        assertNotEquals(null, coord);
    }

    @Test
    void givenCoord_whenCompareWithNotCoord_assertNotEquals() {
        assertNotEquals(new Object(), coord);
    }

    @Test
    void givenCoord_whenAddCoord_assertCorrect() {
        final int diffRow = 1;
        final int diffCol = 2;
        final Coordinate expected = Pa19FactoryHelper.createCoordinate(
                coord.getRow() + diffRow,
                coord.getCol() + diffCol
        );

        assertEquals(expected, coord.add(Pa19FactoryHelper.createCoordinate(diffRow, diffCol)));
    }

    @Test
    void givenCoord_whenAddNegativeCoord_assertCorrect() {
        final int diffRow = -1;
        final int diffCol = -2;
        final Coordinate expected = Pa19FactoryHelper.createCoordinate(
                coord.getRow() + diffRow,
                coord.getCol() + diffCol
        );

        assertEquals(expected, coord.add(Pa19FactoryHelper.createCoordinate(diffRow, diffCol)));
    }

    @AfterEach
    void tearDown() {
        coord = null;
    }
}

import edu.pa19.Cell;
import edu.pa19.Coordinate;
import edu.pa19.FillableCell;
import edu.pa19.Map;
import edu.pa19.Pa19Factory;

import edu.pa19.Pipe;
import edu.pa19.PipeShape;
import edu.pa19.TerminationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapTest {

    /**
     * Tests whether the first pipe can be filled when connected with the source with the correct direction.
     * <p>
     * Succeeds if the pipe is filled.
     * </p>
     */
    @Test
    void givenFirstPipe_ifCanFillPipeFromCorrectDirection_thenSuccess() {
        final var cellRep =
                "WWWW\n" +
                        "W.<W\n" +
                        "W..>\n" +
                        "WWWW";
        final var map = assertDoesNotThrow(() -> createMapFromString(4, 4, cellRep));

        assertTrue(() -> tryPlacePipe(map, 1, 1, createPipe(PipeShape.BOTTOM_RIGHT)));

        map.fillBeginTile();
        map.fillTiles(2);

        assertTrue(() -> map.getCell()[1][1] instanceof FillableCell);

        final var cell = (FillableCell) map.getCell()[1][1];
        assertTrue(() -> cell.getPipe() != null && cell.getPipe().isFilled());
    }

    /**
     * Tests whether the first pipe can be filled when not connected with the source due to incorrect direction.
     * <p>
     * Succeeds if the first pipe is not filled.
     * </p>
     */
    @Test
    void givenFirstPipe_ifCanFillPipeFromIncorrectDirection_thenFail() {
        final var cellRep =
                "WWWW\n" +
                        "W.<W\n" +
                        "W..>\n" +
                        "WWWW";
        final var map = assertDoesNotThrow(() -> createMapFromString(4, 4, cellRep));

        assertTrue(() -> tryPlacePipe(map, 1, 1, createPipe(PipeShape.VERTICAL)));

        map.fillBeginTile();
        map.fillTiles(2);

        assertTrue(() -> map.getCell()[1][1] instanceof FillableCell);

        final var cell = (FillableCell) map.getCell()[1][1];
        assertFalse(() -> cell.getPipe() != null && cell.getPipe().isFilled());
    }

    /**
     * Tests whether a non-first pipes can be filled when connected with the previous pipe with the correct direction.
     * <p>
     * Succeeds if the pipe is filled.
     * </p>
     */
    @Test
    void givenSubsequentPipe_ifCanFillPipeFromCorrectDirection_thenSuccess() {
        final var cellRep =
                "WWWW\n" +
                        "W.<W\n" +
                        "W..>\n" +
                        "WWWW";
        final var map = assertDoesNotThrow(() -> createMapFromString(4, 4, cellRep));

        assertTrue(() -> tryPlacePipe(map, 1, 1, createPipe(PipeShape.BOTTOM_RIGHT)));
        assertTrue(() -> tryPlacePipe(map, 2, 1, createPipe(PipeShape.TOP_RIGHT)));

        map.fillBeginTile();
        map.fillTiles(2);

        assertTrue(() -> map.getCell()[1][1] instanceof FillableCell);
        final var firstCell = (FillableCell) map.getCell()[1][1];
        assertTrue(() -> firstCell.getPipe() != null && firstCell.getPipe().isFilled());

        assertTrue(() -> map.getCell()[2][1] instanceof FillableCell);
        final var secondCell = (FillableCell) map.getCell()[2][1];
        assertTrue(() -> secondCell.getPipe() != null && secondCell.getPipe().isFilled());
    }

    /**
     * Tests whether a non-first pipe can be filled when not connected with the previous pipe due to incorrect
     * direction.
     * <p>
     * Succeeds if the pipe is not filled.
     * </p>
     */
    @Test
    void givenSubsequentPipe_ifCanFillPipeFromIncorrectDirection_thenFail() {
        final var cellRep =
                "WWWW\n" +
                        "W.<W\n" +
                        "W..>\n" +
                        "WWWW";
        final var map = assertDoesNotThrow(() -> createMapFromString(4, 4, cellRep));

        assertTrue(() -> tryPlacePipe(map, 1, 1, createPipe(PipeShape.BOTTOM_RIGHT)));
        assertTrue(() -> tryPlacePipe(map, 2, 1, createPipe(PipeShape.BOTTOM_RIGHT)));

        map.fillBeginTile();
        map.fillTiles(2);

        assertTrue(() -> map.getCell()[1][1] instanceof FillableCell);
        final var firstCell = (FillableCell) map.getCell()[1][1];
        assertTrue(() -> firstCell.getPipe() != null && firstCell.getPipe().isFilled());

        assertTrue(() -> map.getCell()[2][1] instanceof FillableCell);
        final var secondCell = (FillableCell) map.getCell()[2][1];
        assertFalse(() -> secondCell.getPipe() != null && secondCell.getPipe().isFilled());
    }

    private static Map createMapFromString(int rows, int cols, String cellsRep) {
        Cell[][] cells = new Cell[rows][cols];
        for (int r = 0; r < rows; ++r) {
            String line = cellsRep.lines().skip(r).findFirst().orElseThrow();
            for (int c = 0; c < cols; ++c) {
                Coordinate coord = Pa19FactoryHelper.createCoordinate(r , c );
                char ch = line.charAt(c);
                TerminationType termType = (r == 0 || r == rows - 1 || c == 0 || c == cols - 1)
                        ? TerminationType.SINK
                        : TerminationType.SOURCE;
                cells[r][c] = Pa19FactoryHelper.cellFromChar(ch, coord, termType);
            }
        }
        return Pa19FactoryHelper.createMap(rows, cols, cells);
    }

    private static Pipe createPipe(PipeShape shape) {
        Pipe pipe = Pa19Factory.eINSTANCE.createPipe();
        pipe.setShape(shape);
        pipe.setFilled(false);
        return pipe;
    }

    private static boolean tryPlacePipe(Map map, int row, int col, Pipe pipe) {
        Coordinate coord = Pa19FactoryHelper.createCoordinate(row , col);
        return map.tryPlacePipe1(coord, pipe);
    }
}

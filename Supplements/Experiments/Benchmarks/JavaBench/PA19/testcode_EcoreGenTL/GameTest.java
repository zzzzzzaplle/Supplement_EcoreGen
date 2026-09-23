import edu.pa19.Cell;
import edu.pa19.CellStack;
import edu.pa19.Coordinate;
import edu.pa19.DelayBar;
import edu.pa19.FillableCell;
import edu.pa19.Game;
import edu.pa19.Map;
import edu.pa19.Pa19Factory;
import edu.pa19.Pipe;
import edu.pa19.PipeQueue;
import edu.pa19.PipeShape;
import edu.pa19.TerminationType;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {

    @Test
    void givenGame_ifPipeCanBePlaced_stepCountIncreases() {
        final var cellRep =
                "WWWW\n" +
                        "W.<W\n" +
                        "W..>\n" +
                        "WWWW";
        final var initPipes = Collections.singletonList(createPipe(PipeShape.CROSS));
        final var game = assertDoesNotThrow(() -> createGameFromString(4, 4, 0, cellRep, initPipes));

        assertEquals(0, game.getNumOfSteps());
        assertTrue(game.placePipe(1, 'A'));
        assertEquals(1, game.getNumOfSteps());
    }

    @Test
    void givenGame_ifSkipPipe_stepCountIncreases() {
        final var cellRep =
                "WWWW\n" +
                        "W.<W\n" +
                        "W..>\n" +
                        "WWWW";
        final var initPipes = Collections.singletonList(createPipe(PipeShape.CROSS));
        final var game = assertDoesNotThrow(() -> createGameFromString(4, 4, 0, cellRep, initPipes));

        assertEquals(0, game.getNumOfSteps());
        game.skipPipe();
        assertEquals(1, game.getNumOfSteps());
    }

    @Test
    void givenGame_ifUndoPipe_stepCountIncreases() {
        final var cellRep =
                "WWWW\n" +
                        "W.<W\n" +
                        "W..>\n" +
                        "WWWW";
        final var initPipes = Collections.singletonList(createPipe(PipeShape.CROSS));
        final var game = assertDoesNotThrow(() -> createGameFromString(4, 4, 0, cellRep, initPipes));

        game.placePipe(1, 'A');
        int preNumOfSteps = game.getNumOfSteps();
        assertTrue(game.undoStep());
        assertEquals(preNumOfSteps + 1, game.getNumOfSteps());
    }

    @Test
    void givenGame_ifPipeCannotBePlaced_stepCountDoesNotChange() {
        final var cellRep =
                "WWWW\n" +
                        "W.<W\n" +
                        "W..>\n" +
                        "WWWW";
        final var initPipes = Collections.singletonList(createPipe(PipeShape.CROSS));
        final var game = assertDoesNotThrow(() -> createGameFromString(4, 4, 0, cellRep, initPipes));

        assertEquals(0, game.getNumOfSteps());
        assertFalse(game.placePipe(1, 'B'));
        assertEquals(0, game.getNumOfSteps());
    }

    private static Game createGameFromString(int rows, int cols, int delay, String cellsRep, List<Pipe> pipes) {
        Pa19Factory factory = Pa19Factory.eINSTANCE;
        Game game = factory.createGame();

        Cell[][] cells = parseCells(rows, cols, cellsRep);
        Map map = Pa19FactoryHelper.createMap(rows, cols, cells);

        PipeQueue pipeQueue = Pa19FactoryHelper.createPipeQueue(pipes);
        DelayBar delayBar = factory.createDelayBar();
        delayBar.setCurrentValue(delay);

        game.setMap(map);
        game.setPipeQueue(pipeQueue);
        game.setDelayBar(delayBar);
        game.setCellStack(factory.createCellStack());

        return game;
    }

    private static Cell[][] parseCells(int rows, int cols, String cellsRep) {
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
        return cells;
    }

    private static Pipe createPipe(PipeShape shape) {
        Pipe pipe = Pa19Factory.eINSTANCE.createPipe();
        pipe.setShape(shape);
        pipe.setFilled(false);
        return pipe;
    }

}

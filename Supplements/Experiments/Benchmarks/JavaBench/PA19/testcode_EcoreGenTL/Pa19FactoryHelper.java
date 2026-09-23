import edu.pa19.*;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

/**
 * Test-side compatibility helper.
 *
 * The production code in src/main uses {@link edu.pa19.Pa19Helper} as the manual helper entrypoint.
 * Existing tests were written against a Pa19FactoryHelper utility; we provide this adapter
 * in the test source set to keep test semantics unchanged.
 */
public final class Pa19FactoryHelper {

    private Pa19FactoryHelper() {
    }

    public static Coordinate createCoordinate(int row, int col) {
        Coordinate coord = Pa19Factory.eINSTANCE.createCoordinate();
        coord.setRow(row);
        coord.setCol(col);
        return coord;
    }

    public static Cell cellFromChar(char c, Coordinate coord, TerminationType terminationType) {
        Pa19Helper helper = Pa19Factory.eINSTANCE.createPa19Helper();
        return helper.cellFromChar(c, coord, terminationType);
    }

    public static Map createMap(int rows, int cols, Cell[][] cells) {
        Pa19Helper helper = Pa19Factory.eINSTANCE.createPa19Helper();
        return helper.createMap(rows, cols, cells);
    }

    public static PipeQueue createPipeQueue(java.util.List<Pipe> pipes) {
        PipeQueue pq = Pa19Factory.eINSTANCE.createPipeQueue();
        if (pipes != null) {
            pq.getPipeQueue().addAll(pipes);
        }
        while (pq.getPipeQueue().size() < pq.getMaxGenLength()) {
            pq.getPipeQueue().add(pq.generateNewPipe());
        }
        return pq;
    }

    public static Game createGame(int rows, int cols, int delay, Cell[][] cells, java.util.List<Pipe> pipes) {
        Pa19Helper helper = Pa19Factory.eINSTANCE.createPa19Helper();
        EList<Pipe> ePipes = new BasicEList<>();
        if (pipes != null) {
            ePipes.addAll(pipes);
        }
        return helper.createGame(rows, cols, delay, cells, ePipes);
    }
}

import edu.pa20.Move;
import edu.pa20.Piece;
import org.eclipse.emf.common.util.EList;

import java.util.Arrays;
import java.util.Objects;

public class Compares {
    public static boolean isBoardEqual(Piece[][] expected, Piece[][] actual) {
        if (expected.length != actual.length) {
            return false;
        }
        for (int i = 0; i < expected.length; i++) {
            if (expected[i].length != actual[i].length) {
                return false;
            }
            for (int j = 0; j < expected[i].length; j++) {
                if (!Objects.equals(expected[i][j], actual[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean areContentsEqual(Object[] expected, Object[] actual) {
        if (expected.length != actual.length) {
            return false;
        }
        for (var item :
                actual) {
            if (!Arrays.asList(expected).contains(item)) {
                return false;
            }
        }
        return true;
    }

    public static boolean areContentsEqual(EList<Move> actual, Move[] expected) {
        if (actual == null) {
            return expected == null || expected.length == 0;
        }
        if (expected == null) {
            return actual.isEmpty();
        }
        if (actual.size() != expected.length) {
            return false;
        }
        for (Move actualMove : actual) {
            boolean found = false;
            for (Move expectedMove : expected) {
                if (actualMove != null && expectedMove != null && actualMove.equal(expectedMove)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public static boolean contentsContain(Object[] array, Object[] contained) {
        return Arrays.asList(array).containsAll(Arrays.asList(contained));
    }
}

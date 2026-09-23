import edu.pa22.GameMap;
import edu.pa22.PA22Helper;
import edu.pa22.Pa22Factory;

import java.nio.file.Files;

/**
 * Helper class for testing.
 */
public class TestHelper {
    /**
     * @param mapText The map text for input.
     * @return The parsed map.
     */
    public static GameMap parseGameMap(String mapText) {

        PA22Helper pa22Helper = Pa22Factory.eINSTANCE.createPA22Helper();
        return pa22Helper.parseGameMap(mapText);
    }
}

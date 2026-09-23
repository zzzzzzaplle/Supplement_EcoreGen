import edu.pa20.Configuration;
import edu.pa20.InvalidConfigurationError;
import edu.pa20.Pa20Factory;
import edu.pa20.Piece;
import edu.pa20.Place;
import edu.pa20.Player;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SampleTest
public class ConfigurationTests {
    @Test
    public void testSizeOdd() {
        assertThrows(InvalidConfigurationError.class, () -> createConfiguration(-1, new Player[]{new MockPlayer(), new MockPlayer()}));
        assertThrows(InvalidConfigurationError.class, () -> createConfiguration(0, new Player[]{new MockPlayer(), new MockPlayer()}));
        assertThrows(InvalidConfigurationError.class, () -> createConfiguration(1, new Player[]{new MockPlayer(), new MockPlayer()}));
        assertThrows(InvalidConfigurationError.class, () -> createConfiguration(2, new Player[]{new MockPlayer(), new MockPlayer()}));
        assertDoesNotThrow(() -> createConfiguration(3, new Player[]{new MockPlayer(), new MockPlayer()}));
    }

    @Test
    public void testPlayersNumber() {
        assertThrows(InvalidConfigurationError.class, () -> createConfiguration(3, new Player[]{new MockPlayer()}));
        assertThrows(InvalidConfigurationError.class, () -> createConfiguration(3, new Player[]{new MockPlayer(), new MockPlayer(),
                new MockPlayer()}));
        assertDoesNotThrow(() -> createConfiguration(3, new Player[]{new MockPlayer(), new MockPlayer(),}));
    }

    @Test
    public void testCentralPlace() {
        assertPlaceEquals(1, 1, createConfiguration(3, new Player[]{new MockPlayer(), new MockPlayer()}).getCentralPlace());
        assertPlaceEquals(2, 2, createConfiguration(5, new Player[]{new MockPlayer(), new MockPlayer()}).getCentralPlace());
        assertPlaceEquals(4, 4, createConfiguration(9, new Player[]{new MockPlayer(), new MockPlayer()}).getCentralPlace());
    }

    @Test
    public void testAddInitialPiece() {
        var player1 = new MockPlayer();
        var player2 = new MockPlayer();
        var piece = new MockPiece(player1);
        assertThrows(InvalidConfigurationError.class, () -> addInitialPiece(createConfiguration(3, new Player[]{new MockPlayer(), new MockPlayer()}), piece, 0, 0));
        assertThrows(InvalidConfigurationError.class, () -> addInitialPiece(createConfiguration(3, new Player[]{player1, player2}), piece, 100, 0));
        var config = createConfiguration(3, new Player[]{player1, player2});
        addInitialPiece(config, piece, 0, 0);
        assertEquals(2, config.getPlayers().size());
        assertEquals(piece, config.getInitialBoard()[0][0]);
    }

    private static void assertPlaceEquals(int expectedX, int expectedY, Place actual) {
        assertNotNull(actual);
        assertEquals(expectedX, actual.getX());
        assertEquals(expectedY, actual.getY());
    }

    private static Configuration createConfiguration(int size, Player[] players) {
        Configuration configuration = Pa20Factory.eINSTANCE.createConfiguration();
        EList<Player> playerList = new BasicEList<>();
        if (players != null) {
            for (Player player : players) {
                playerList.add(player);
            }
        }
        try {
            configuration.InitConfiguration(size, playerList, 0);
        } catch (IllegalArgumentException ex) {
            throw new InvalidConfigurationError(ex.getMessage());
        }
        return configuration;
    }

    private static void addInitialPiece(Configuration configuration, Piece piece, int x, int y) {
        try {
            configuration.addInitialPiece(piece, x, y);
        } catch (IllegalArgumentException ex) {
            throw new InvalidConfigurationError(ex.getMessage());
        }
    }
}

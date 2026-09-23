package edu.pa22;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Factory for creating Sokoban games
 */
public class SokobanGameFactory {
  
    /**
     * Create a TUI version of the Sokoban game.
     *
     * @param mapFile map file.
     * @return The Sokoban game.
     * @throws IOException if mapFile cannot be load
     */
    public static SokobanGame createTUIGame(String mapFile) throws IOException {
        PA22Helper helper = Pa22Factory.eINSTANCE.createPA22Helper();
    		GameMap gameMap = null;
        if (!mapFile.endsWith(".map")) {
            // treat as built-in maps
            final String resourceName = mapFile + ".map";
            try (InputStream in = SokobanGameFactory.class.getClassLoader().getResourceAsStream(resourceName)) {
                if (in == null) {
                    throw new RuntimeException("No such built-in map: " + mapFile);
                }
                final String fileContent = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                try {
					gameMap = helper.parseGameMap(fileContent);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        } else {
            gameMap = loadGameMap(Path.of(mapFile));
        }

        final GameState gameState = Pa22Factory.eINSTANCE.createGameState();
        gameState.setUndoQuota(gameMap.getUndoLimit());
        gameState.setBoardWidth(gameMap.getMaxWidth());
        gameState.setBoardHeight(gameMap.getMaxHeight());
        gameState.getDestinations().addAll(gameMap.getDestinations());
        gameState.setEntities(gameMap.getMap());
        gameState.setCurrentTransition(Pa22Factory.eINSTANCE.createGameStateTransition());

        final TerminalInputEngine inputEngine = Pa22Factory.eINSTANCE.createTerminalInputEngine();
        inputEngine.setTerminalScanner(new Scanner(System.in));

        final TerminalRenderingEngine renderingEngine = Pa22Factory.eINSTANCE.createTerminalRenderingEngine();
        renderingEngine.setOutputStream(System.out);

        final TerminalSokobanGame game = Pa22Factory.eINSTANCE.createTerminalSokobanGame();
        game.setState(gameState);
        game.setInputEngine(inputEngine);
        game.setRenderingEngine(renderingEngine);
        return game;
    }


    /**
     * @param mapFile The file containing the game map.
     * @return The parsed game map.
     * @throws IOException When there is an issue loading the file.
     */
    public static GameMap loadGameMap(Path mapFile) throws IOException {
        final String fileContent = Files.readString(mapFile);
        PA22Helper helper = Pa22Factory.eINSTANCE.createPA22Helper();
        return helper.parseGameMap(fileContent);
    }

}

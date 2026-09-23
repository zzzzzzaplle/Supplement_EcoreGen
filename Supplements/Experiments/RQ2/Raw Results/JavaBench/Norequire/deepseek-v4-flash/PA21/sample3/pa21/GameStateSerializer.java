/**
 * 适配 EMF 的序列化工具逻辑
 */
package edu.pa21;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameStateSerializer {

    private GameStateSerializer() {
    }
public static void writeTo(final GameState gameState, final java.io.BufferedWriter writer) throws java.io.IOException {
    if (gameState == null || writer == null) return;

    // EMF 访问器调
    writer.write(Integer.toString(gameState.getGameBoard().getNumRows()));
    writer.newLine();
    writer.write(Integer.toString(gameState.getGameBoard().getNumCols()));
    writer.newLine();
    
    if (gameState.hasUnlimitedLives()) {
        writer.write("");
    } else {
        writer.write(Integer.toString(gameState.getNumLives()));
    }
    writer.newLine();

    final Cell[][] board = gameState.getGameBoard().getBoard();
    for (int r = 0; r < gameState.getGameBoard().getNumRows(); ++r) {
        for (int c = 0; c < gameState.getGameBoard().getNumCols(); ++c) {
            writer.write(toCellChar(board[r][c]));
        }
        writer.newLine();
    }
}

public static GameState loadFrom(final java.io.BufferedReader reader) throws java.io.IOException {
    final int numRows = Integer.parseInt(reader.readLine());
    final int numCols = Integer.parseInt(reader.readLine());
    final int numLives;
    final String line = reader.readLine();
    numLives = (line == null || line.isBlank()) ? -1 : Integer.parseInt(line);

    // 1. 使用 Factory 创建 GameBoard
    GameBoard gameBoard = edu.pa21.Pa21Factory.eINSTANCE.createGameBoard();
    gameBoard.setNumRows(numRows);
    gameBoard.setNumCols(numCols);

    Cell[][] board = new Cell[numRows][numCols];
    for (int r = 0; r < numRows; r++) {
        final String boardLine = reader.readLine();
        for (int c = 0; c < numCols; ++c) {
            Position pos = edu.pa21.Pa21Factory.eINSTANCE.createPosition();
            pos.setRow(r);
            pos.setCol(c);
            board[r][c] = fromCellChar(boardLine.charAt(c), pos);
        }
    }
    gameBoard.setBoard(board);

    // 2. 使用 Factory 创建 GameState
    GameState gameState = edu.pa21.Pa21Factory.eINSTANCE.createGameState();
    gameState.setGameBoard(gameBoard);
    // 这里调用你重命名后的 lives 属性或逻辑方法
    if (numLives >= 0) {
        gameState.setNumLives(numLives); 
    } else {
        gameState.setNumLives(-1);
    }
    return gameState;
}

/**
 * 便捷重载：从路径打开文件并委托给 loadFrom
 */
public static GameState loadFrom(final Path path) throws FileNotFoundException {
    try {
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Cannot find file " + path + " to load");
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return loadFrom(reader);
        }
    } catch (FileNotFoundException e) {
        throw e;
    } catch (IOException e) {
        throw new RuntimeException("Failed to load game state from " + path, e);
    }
}

private static char toCellChar(final Cell cell) {
    if (cell instanceof Wall) return 'W';
    if (cell instanceof EntityCell) {
        EntityCell ec = (EntityCell) cell;
        Entity e = ec.getEntity();
        if (e instanceof ExtraLife) return 'L';
        if (e instanceof Gem) return 'G';
        if (e instanceof Mine) return 'M';
        if (e instanceof Player) return 'P';
        if (cell instanceof StopCell) return 'S';
    }
    return '.';
}

private static Cell fromCellChar(final char c, final Position position) {
    edu.pa21.Pa21Factory factory = edu.pa21.Pa21Factory.eINSTANCE;
    switch (c) {
        case 'W':
            Wall wall = factory.createWall();
            wall.setPosition(position);
            return wall;
        case 'L':
            EntityCell ecL = factory.createEntityCell();
            ecL.setPosition(position);
            ecL.setEntity(factory.createExtraLife());
            return ecL;
        case 'G':
            EntityCell ecG = factory.createEntityCell();
            ecG.setPosition(position);
            ecG.setEntity(factory.createGem());
            return ecG;
        case 'M':
            EntityCell ecM = factory.createEntityCell();
            ecM.setPosition(position);
            ecM.setEntity(factory.createMine());
            return ecM;
        case 'P':
            StopCell scP = factory.createStopCell();
            scP.setPosition(position);
            scP.setEntity(factory.createPlayer());
            return scP;
        case 'S':
            StopCell sc = factory.createStopCell();
            sc.setPosition(position);
            return sc;
        default:
            EntityCell ecDef = factory.createEntityCell();
            ecDef.setPosition(position);
            return ecDef;
    }
}
}

public class GameStateSerializer {

    private GameStateSerializer() {
    }

    /**
     * Serializes the specified {@link GameState} object to the output file.
     *
     * @param gameState  The game state instance to write to the file.
     * @param outputFile The file to write to.
     * @return {@code outputFile}.
     */
    public static java.nio.file.Path writeTo(final GameState gameState, final java.nio.file.Path outputFile)
            throws java.nio.file.FileAlreadyExistsException {
        java.util.Objects.requireNonNull(gameState);
        java.util.Objects.requireNonNull(outputFile);

        if (java.nio.file.Files.exists(outputFile)) {
            throw new java.nio.file.FileAlreadyExistsException(outputFile.toString());
        }

        try (java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(outputFile)) {
            writeTo(gameState, writer);
        } catch (final java.io.IOException e) {
            throw new RuntimeException(e);
        }
        return outputFile;
    }

    static void writeTo(final GameState gameState, final java.io.BufferedWriter writer)
            throws java.io.IOException {
        java.util.Objects.requireNonNull(gameState);
        java.util.Objects.requireNonNull(writer);

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

        for (int r = 0; r < gameState.getGameBoard().getNumRows(); ++r) {
            final Cell[] row = gameState.getGameBoard().getRow(r);

            for (final Cell cell : row) {
                writer.write(toCellChar(cell));
            }
            writer.newLine();
        }
    }

    public static GameState loadFrom(final java.nio.file.Path inputFile)
            throws java.io.FileNotFoundException {
        java.util.Objects.requireNonNull(inputFile);

        if (!java.nio.file.Files.isRegularFile(inputFile)) {
            throw new java.io.FileNotFoundException(inputFile.toString());
        }

        try (java.io.BufferedReader reader = java.nio.file.Files.newBufferedReader(inputFile)) {
            return loadFrom(reader);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    static GameState loadFrom(final java.io.BufferedReader reader) throws java.io.IOException {
        java.util.Objects.requireNonNull(reader);

        final int numRows = Integer.parseInt(reader.readLine());
        final int numCols = Integer.parseInt(reader.readLine());
        final int numLives;
        {
            final String line = reader.readLine();
            if (line.isBlank()) {
                numLives = -1;
            } else {
                numLives = Integer.parseInt(line);
            }
        }

        final Cell[][] board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            final String line = reader.readLine();
            for (int c = 0; c < numCols; ++c) {
                board[r][c] = fromCellChar(line.charAt(c), new Position(r, c));
            }
        }

        final GameBoard gameBoard = new GameBoard(numRows, numCols, board);
        return numLives < 0 ? new GameState(gameBoard) : new GameState(gameBoard, numLives);
    }

    private static char toCellChar(final Cell cell) {
        java.util.Objects.requireNonNull(cell);

        if (cell instanceof Wall) {
            return 'W';
        }

        final EntityCell cellWithEntity = (EntityCell) cell;
        final Entity entity = cellWithEntity.getEntity();
        if (entity instanceof ExtraLife) {
            return 'L';
        }
        if (entity instanceof Gem) {
            return 'G';
        }
        if (entity instanceof Mine) {
            return 'M';
        }
        if (entity instanceof Player) {
            return 'P';
        }

        if (cellWithEntity instanceof StopCell) {
            return 'S';
        }
        return '.';
    }

    private static Cell fromCellChar(final char c, final Position position) {
        java.util.Objects.requireNonNull(position);

        return switch (c) {
            case 'W' -> new Wall(position);
            case 'L' -> new EntityCell(position, new ExtraLife());
            case 'G' -> new EntityCell(position, new Gem());
            case 'M' -> new EntityCell(position, new Mine());
            case 'P' -> new StopCell(position, new Player());
            case 'S' -> new StopCell(position);
            case '.' -> new EntityCell(position);
            default -> throw new IllegalArgumentException("Unknown cell representation: " + c);
        };
    }
}

package edu.pa19;

import edu.pa19.*;
import edu.pa19.Coordinate;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;

/**
 * A deserializer for converting a map file into a {@link game.Game}.
 */
public class Deserializer {

    private Path path;

    public Deserializer(final String path) throws FileNotFoundException {
        this(Paths.get(path));
    }

    private Deserializer(final Path path) throws FileNotFoundException {
        if (!path.toFile().exists()) {
            throw new FileNotFoundException("Cannot find file to load!");
        }

        this.path = path;
    }

    /**
     * Parses the text file and returns an instance of {@link game.Game}.
     *
     * @return An instance of {@link game.Game}, or {@code null} if the file cannot be parsed.
     */
    public Game parseGame() {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;

            int rows = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                rows = Integer.parseInt(line);
            } else {
                throw new EOFException();
            }

            int cols = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                cols = Integer.parseInt(line);
            } else {
                throw new EOFException();
            }

            int delay = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                delay = Integer.parseInt(line);
            } else {
                throw new EOFException();
            }

            final ArrayList<String> mapRep = new ArrayList<String>();
            for (int r = 0; r < rows; ++r) {
                line = getFirstNonEmptyLine(reader);
                if (line == null) {
                    throw new EOFException();
                }

                mapRep.add(line);
            }
            
            final Cell[][] cells = parseString(rows, cols, String.join("\n", mapRep));

            // 1. 实例化 Helper 以调用实例方法
            Pa19Helper helper = Pa19Factory.eINSTANCE.createPa19Helper();
            EList<Pipe> defaultPipes = null;
            
            String s = getFirstNonEmptyLine(reader);
            if (s != null) {
                defaultPipes = (EList<Pipe>) Arrays.stream(s.split(","))
                        // 2. 使用 helper 实例的方法引用
                        .map(helper::pipeFromString)
                        .collect(Collectors.toList());
            }
            
            // 3. 使用 helper 实例去组装 Game
            return helper.createGame(rows, cols, delay, cells, defaultPipes);
            
        } catch (EOFException eofe) {
            System.err.println("Unexpected EOF");
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * Deserializes a map from a {@link java.lang.String}.
     *
     * @param rows Rows of the given map.
     * @param cols Columns of the given map.
     * @param cellsRep String representation of the map, with rows delimited by {@code '\n'}.
     * @return A 2D cell array from the string. Note that this cell array may not fully conform to the requirements of
     * an actual game map; The "map conformance" checks are performed in the {@link game.map.Map} constructor.
     */
    public static Cell[][] parseString(final int rows, final int cols, final String cellsRep) {
        Cell[][] cells = new Cell[rows][cols];
        
        // 4. 因为此方法是 static，所以也需要实例化 Helper
        Pa19Helper helper = new Pa19Helper();
        
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                // 5. 直接使用 EMF 工厂创建 Coordinate 并赋值
                Coordinate coord = Pa19Factory.eINSTANCE.createCoordinate();
                coord.setRow(r);
                coord.setCol(c);
                    
                char ch = cellsRep.lines().skip(r).findFirst().orElseThrow().charAt(c);

                Cell cell;
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    // 6. 使用 helper 实例调用 cellFromChar
                    cell = helper.cellFromChar(ch, coord, TerminationType.SINK);
                } else {
                    cell = helper.cellFromChar(ch, coord, TerminationType.SOURCE);
                }

                cells[r][c] = cell;
            }
        }

        return cells;
    }

    /**
     * Returns the first non-empty and non-comment line from the reader.
     *
     * @param br {@link java.io.BufferedReader} to read from.
     * @return First line that is a parseable line, or {@code null} there are no lines to read.
     * @throws IOException if the reader fails to read a line.
     */
    private String getFirstNonEmptyLine(final BufferedReader br) throws IOException {
        do {

            String s = br.readLine();

            if (s == null) {
                return null;
            }
            if (s.isBlank() || s.startsWith("#")) {
                continue;
            }

            return s;
        } while (true);
    }
}
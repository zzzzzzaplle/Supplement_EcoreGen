import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A deserializer for converting a map file into a {@link Game}.
 */
public class Deserializer {

    
    private Path path;

    public Deserializer(final String path) throws FileNotFoundException {
        this(Paths.get(path));
    }

    public Deserializer() {}

    private Deserializer( final Path path) throws FileNotFoundException {
        if (!path.toFile().exists()) {
            throw new FileNotFoundException("Cannot find file to load!");
        }

        this.path = path;
    }


    public Game parseGame() {
        try (var reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;

            var rows = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                rows = Integer.parseInt(line);
            } else {
                throw new EOFException();
            }

            var cols = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                cols = Integer.parseInt(line);
            } else {
                throw new EOFException();
            }

            var delay = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                delay = Integer.parseInt(line);
            } else {
                throw new EOFException();
            }

            final var mapRep = new ArrayList<String>();
            for (int r = 0; r < rows; ++r) {
                line = getFirstNonEmptyLine(reader);
                if (line == null) {
                    throw new EOFException();
                }

                mapRep.add(line);
            }
            final var cells = parseString(rows, cols, String.join("\n", mapRep));

            List<Pipe> defaultPipes = null;
            String s = getFirstNonEmptyLine(reader);
            if (s != null) {
                defaultPipes = Arrays.stream(s.split(","))
                        .map(Pipe::fromString)
                        .collect(Collectors.toList());
            }

            return new Game(rows, cols, delay, cells, defaultPipes);
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
     * an actual game map; The "map conformance" checks are performed in the {@link Map} constructor.
     */
     public static Cell[][] parseString(final int rows, final int cols, final String cellsRep) {
        var cells = new Cell[rows][cols];
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                var coord = new Coordinate(r, c);
                var ch = cellsRep.lines().skip(r).findFirst().orElseThrow().charAt(c);

                Cell cell;
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    cell = Cell.fromChar(ch, coord, TerminationType.SINK);
                } else {
                    cell = Cell.fromChar(ch, coord, TerminationType.SOURCE);
                }

                cells[r][c] = cell;
            }
        }

        return cells;
    }


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

    public Path getPath() { return path; }
    public void setPath(Path path) { this.path = path; }
}

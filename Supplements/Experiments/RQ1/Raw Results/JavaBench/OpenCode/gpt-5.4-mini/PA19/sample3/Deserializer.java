public class Deserializer {
    public Deserializer() {
    }

    public static Cell[][] parseString(int rows, int cols, String cellsRep) {
        Cell[][] cells = new Cell[rows][cols];
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                Coordinate coord = new Coordinate(r, c);
                char ch = cellsRep.lines().skip(r).findFirst().orElseThrow().charAt(c);
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
}

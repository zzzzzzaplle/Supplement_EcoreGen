/**
 * Information holder for creating a TerminationCell.
 */
public class TerminationCellCreateInfo {
    public Coordinate coord;
    public Direction dir;

    public TerminationCellCreateInfo() {
    }

    public TerminationCellCreateInfo(Coordinate coord, Direction dir) {
        this.coord = coord;
        this.dir = dir;
    }
}

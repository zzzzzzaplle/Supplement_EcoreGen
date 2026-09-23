/**
 * Lightweight data carrier for constructing a TerminationCell.
 */
public class TerminationCellCreateInfo {

    public Coordinate coord;
    public Direction dir;

    public TerminationCellCreateInfo() {
        this.coord = new Coordinate();
        this.dir = Direction.UP;
    }

    public TerminationCellCreateInfo(Coordinate coord, Direction dir) {
        this.coord = coord;
        this.dir = dir;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }
}

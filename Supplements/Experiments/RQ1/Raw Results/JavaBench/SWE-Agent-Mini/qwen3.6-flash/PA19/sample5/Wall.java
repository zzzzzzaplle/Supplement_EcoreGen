import java.util.Optional;

/**
 * Wall cell that forms the outer boundary of the map.
 */
public class Wall extends Cell {

    public Wall() {
        super();
    }

    public Wall(Coordinate coord) {
        super(coord);
    }

    @Override
    public char toSingleChar() {
        return PipePatterns.WALL;
    }

    @Override
    public Optional<Pipe> getPipe() {
        return Optional.empty();
    }

    @Override
    public void setPipe(Pipe pipe) {
        // Walls cannot hold pipes
    }
}

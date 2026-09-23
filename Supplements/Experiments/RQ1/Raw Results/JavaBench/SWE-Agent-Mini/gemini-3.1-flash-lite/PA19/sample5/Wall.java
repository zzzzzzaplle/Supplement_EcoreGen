public class Wall extends Cell {
    public Wall() {}

    @Override
    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}

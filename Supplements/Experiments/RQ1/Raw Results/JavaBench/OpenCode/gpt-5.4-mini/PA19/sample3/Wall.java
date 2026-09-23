public class Wall extends Cell implements MapElement {
    public Wall() {
    }

    public char toSingleChar() {
        return PipePatterns.WALL;
    }
}

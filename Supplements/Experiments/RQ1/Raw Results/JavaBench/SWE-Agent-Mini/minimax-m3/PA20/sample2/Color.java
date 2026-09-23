public enum Color {
    DEFAULT,
    BLACK,
    RED,
    GREEN,
    YELLOW,
    BLUE,
    PURPLE,
    CYAN,
    WHITE;

    private String ansiColor;

    static {
        DEFAULT.ansiColor = "\u001B[0m";
        BLACK.ansiColor = "\u001B[30m";
        RED.ansiColor = "\u001B[31m";
        GREEN.ansiColor = "\u001B[32m";
        YELLOW.ansiColor = "\u001B[33m";
        BLUE.ansiColor = "\u001B[34m";
        PURPLE.ansiColor = "\u001B[35m";
        CYAN.ansiColor = "\u001B[36m";
        WHITE.ansiColor = "\u001B[37m";
    }

    public String getAnsiColor() {
        return ansiColor;
    }

    public void setAnsiColor(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    @Override
    public String toString() {
        return ansiColor;
    }
}

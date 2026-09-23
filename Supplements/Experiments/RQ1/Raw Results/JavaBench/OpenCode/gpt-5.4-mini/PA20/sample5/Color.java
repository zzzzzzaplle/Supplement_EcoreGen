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

    Color() {
    }

    public String getAnsiColor() {
        return this.ansiColor;
    }

    public void setAnsiColor(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    public String toString() {
        return name();
    }
}

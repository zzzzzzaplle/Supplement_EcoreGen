public enum Color {
    DEFAULT("\033[0m"),
    BLACK("\033[30m"),
    RED("\033[31m"),
    GREEN("\033[32m"),
    YELLOW("\033[33m"),
    BLUE("\033[34m"),
    PURPLE("\033[35m"),
    CYAN("\033[36m"),
    WHITE("\033[37m");

    private String ansiColor;

    Color(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    public String toString() {
        return ansiColor;
    }
}

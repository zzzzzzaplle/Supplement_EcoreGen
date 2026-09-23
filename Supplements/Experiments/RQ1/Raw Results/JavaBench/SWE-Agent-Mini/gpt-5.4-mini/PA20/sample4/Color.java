public enum Color {
    DEFAULT("\u001b[0m"),
    BLACK("\u001b[30m"),
    RED("\u001b[31m"),
    GREEN("\u001b[32m"),
    YELLOW("\u001b[33m"),
    BLUE("\u001b[34m"),
    PURPLE("\u001b[35m"),
    CYAN("\u001b[36m"),
    WHITE("\u001b[37m");

    private String ansiColor;

    Color(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    Color() {
        this.ansiColor = "";
    }

    public String getAnsiColor() {
        return this.ansiColor;
    }

    public void setAnsiColor(String ansiColor) {
        this.ansiColor = ansiColor;
    }

    @Override
    public String toString() {
        return this.ansiColor;
    }
}

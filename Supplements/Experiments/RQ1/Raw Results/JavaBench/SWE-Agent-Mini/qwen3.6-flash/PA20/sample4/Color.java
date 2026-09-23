import java.util.Objects;

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

    @Override
    public String toString() {
        switch (this) {
            case BLACK:
                ansiColor = "\u001b[30m";
                return "\u001b[30m";
            case RED:
                ansiColor = "\u001b[31m";
                return "\u001b[31m";
            case GREEN:
                ansiColor = "\u001b[32m";
                return "\u001b[32m";
            case YELLOW:
                ansiColor = "\u001b[33m";
                return "\u001b[33m";
            case BLUE:
                ansiColor = "\u001b[34m";
                return "\u001b[34m";
            case PURPLE:
                ansiColor = "\u001b[35m";
                return "\u001b[35m";
            case CYAN:
                ansiColor = "\u001b[36m";
                return "\u001b[36m";
            case WHITE:
                ansiColor = "\u001b[37m";
                return "\u001b[37m";
            default:
                ansiColor = "";
                return "";
        }
    }
}

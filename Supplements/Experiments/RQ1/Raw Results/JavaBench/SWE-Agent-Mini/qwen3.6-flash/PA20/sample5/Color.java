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
        switch (this) {
            case DEFAULT:
                ansiColor = "";
                break;
            case BLACK:
                ansiColor = "\u001b[30m";
                break;
            case RED:
                ansiColor = "\u001b[31m";
                break;
            case GREEN:
                ansiColor = "\u001b[32m";
                break;
            case YELLOW:
                ansiColor = "\u001b[33m";
                break;
            case BLUE:
                ansiColor = "\u001b[34m";
                break;
            case PURPLE:
                ansiColor = "\u001b[35m";
                break;
            case CYAN:
                ansiColor = "\u001b[36m";
                break;
            case WHITE:
                ansiColor = "\u001b[37m";
                break;
            default:
                ansiColor = "";
                break;
        }
    }

    public String toString() {
        return ansiColor + this.name() + "\u001b[0m";
    }
}

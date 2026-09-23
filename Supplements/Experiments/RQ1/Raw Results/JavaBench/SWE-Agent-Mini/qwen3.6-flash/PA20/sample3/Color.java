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
            case BLACK:
                this.ansiColor = "\u001b[30m";
                break;
            case RED:
                this.ansiColor = "\u001b[31m";
                break;
            case GREEN:
                this.ansiColor = "\u001b[32m";
                break;
            case YELLOW:
                this.ansiColor = "\u001b[33m";
                break;
            case BLUE:
                this.ansiColor = "\u001b[34m";
                break;
            case PURPLE:
                this.ansiColor = "\u001b[35m";
                break;
            case CYAN:
                this.ansiColor = "\u001b[36m";
                break;
            case WHITE:
                this.ansiColor = "\u001b[37m";
                break;
            default:
                this.ansiColor = "";
                break;
        }
    }
    
    @Override
    public String toString() {
        return this.ansiColor;
    }
}

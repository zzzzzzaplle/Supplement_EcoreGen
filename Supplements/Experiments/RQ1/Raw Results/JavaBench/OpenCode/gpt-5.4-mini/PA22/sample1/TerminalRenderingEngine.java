public class TerminalRenderingEngine implements RenderingEngine {
    private java.io.PrintStream outputStream;

    public TerminalRenderingEngine() {
    }

    public TerminalRenderingEngine(java.io.PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public void render(GameState state) {
    }

    public void message(String content) {
    }

    public java.io.PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(java.io.PrintStream outputStream) {
        this.outputStream = outputStream;
    }
}

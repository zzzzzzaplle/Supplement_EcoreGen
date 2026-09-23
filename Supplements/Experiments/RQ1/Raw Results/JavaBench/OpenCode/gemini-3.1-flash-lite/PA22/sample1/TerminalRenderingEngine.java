import java.io.PrintStream;

public class TerminalRenderingEngine implements RenderingEngine {
    private java.io.PrintStream outputStream;

    public TerminalRenderingEngine() {}

    public TerminalRenderingEngine(java.io.PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public java.io.PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(java.io.PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {}

    @Override
    public void message(String content) {}
}

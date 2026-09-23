import java.io.PrintStream;

public class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public TerminalRenderingEngine() {
    }

    public void render(GameState state) {
    }

    public void message(String content) {
    }
}

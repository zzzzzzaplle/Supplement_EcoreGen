import java.io.PrintStream;

public class TerminalRenderingEngine implements RenderingEngine {
    private PrintStream outputStream;

    public TerminalRenderingEngine() {}

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    public void render(GameState state) {}
    public void message(String content) { outputStream.println(content); }

    public PrintStream getOutputStream() { return outputStream; }
    public void setOutputStream(PrintStream outputStream) { this.outputStream = outputStream; }
}

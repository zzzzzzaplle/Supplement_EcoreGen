import java.io.PrintStream;

public class TerminalRenderingEngine implements RenderingEngine {
    PrintStream outputStream;

    public TerminalRenderingEngine() {
    }

    public TerminalRenderingEngine(PrintStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void render(GameState state) {
        throw new NotImplementedException();
    }

    @Override
    public void message(String content) {
        outputStream.println(content);
    }
}

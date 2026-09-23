import java.io.InputStream;
import java.util.Scanner;

public class TerminalInputEngine implements InputEngine {
    Scanner terminalScanner;

    public TerminalInputEngine() {
    }

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() {
        throw new NotImplementedException();
    }
}

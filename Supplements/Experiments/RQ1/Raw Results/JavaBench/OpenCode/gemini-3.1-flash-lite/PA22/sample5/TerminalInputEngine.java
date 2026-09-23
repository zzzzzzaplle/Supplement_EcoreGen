import java.io.InputStream;
import java.util.Scanner;

public class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() { return null; }

    public Scanner getTerminalScanner() { return terminalScanner; }
    public void setTerminalScanner(Scanner terminalScanner) { this.terminalScanner = terminalScanner; }
}

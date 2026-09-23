import java.io.InputStream;
import java.util.Scanner;

public class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine() {}
    public TerminalInputEngine(InputStream terminalStream) { this.terminalScanner = new Scanner(terminalStream); }

    public Scanner getTerminalScanner() { return terminalScanner; }
    public void setTerminalScanner(Scanner terminalScanner) { this.terminalScanner = terminalScanner; }

    public Action fetchAction() { return null; }
}

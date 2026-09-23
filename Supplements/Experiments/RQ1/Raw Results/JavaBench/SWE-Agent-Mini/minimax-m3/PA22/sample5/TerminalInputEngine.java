import java.io.InputStream;
import java.util.Scanner;

public class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine() {
        this.terminalScanner = new Scanner(System.in);
    }

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    public Scanner getTerminalScanner() {
        return terminalScanner;
    }

    public void setTerminalScanner(Scanner terminalScanner) {
        this.terminalScanner = terminalScanner;
    }

    @Override
    public Action fetchAction() {
        if (!terminalScanner.hasNextLine()) {
            return new Exit(-1);
        }
        String line = terminalScanner.nextLine().trim();
        if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
            return new Exit(-1);
        }
        if (line.isEmpty()) {
            return new InvalidInput(-1, "Empty input");
        }
        char c = line.charAt(0);
        switch (c) {
            case 'w': case 'W':
                return new Up(0);
            case 's': case 'S':
                return new Down(0);
            case 'a': case 'A':
                return new Left(0);
            case 'd': case 'D':
                return new Right(0);
            case 'r': case 'R':
                return new Undo(0);
            case 'k': case 'K':
                return new Up(1);
            case 'j': case 'J':
                return new Down(1);
            case 'h': case 'H':
                return new Left(1);
            case 'l': case 'L':
                return new Right(1);
            case 'u': case 'U':
                return new Undo(1);
            default:
                return new InvalidInput(-1, "Unknown command: " + line);
        }
    }
}

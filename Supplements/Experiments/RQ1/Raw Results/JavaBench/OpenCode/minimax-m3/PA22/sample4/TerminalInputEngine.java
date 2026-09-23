import java.io.InputStream;
import java.util.Scanner;

public class TerminalInputEngine implements InputEngine {

    private Scanner terminalScanner;

    public TerminalInputEngine() {
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
        if (terminalScanner == null) {
            return new InvalidInput(-1, "No input stream");
        }
        if (!terminalScanner.hasNextLine()) {
            return new Exit(-1);
        }
        String line = terminalScanner.nextLine().trim().toLowerCase();
        if (line.isEmpty()) {
            return new InvalidInput(-1, "Invalid input");
        }
        switch (line) {
            case "w": return new Up(0);
            case "a": return new Left(0);
            case "s": return new Down(0);
            case "d": return new Right(0);
            case "r": return new Undo(0);
            case "k": return new Up(1);
            case "h": return new Left(1);
            case "j": return new Down(1);
            case "l": return new Right(1);
            case "u": return new Undo(1);
            case "exit":
            case "quit":
                return new Exit(-1);
            default:
                return new InvalidInput(-1, "Invalid input");
        }
    }
}

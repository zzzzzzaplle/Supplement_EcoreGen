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
        if (terminalScanner == null) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        if (!terminalScanner.hasNextLine()) {
            return new Exit(-1);
        }
        String line = terminalScanner.nextLine().trim();
        if (line.equalsIgnoreCase(StringResources.EXIT_COMMAND_TEXT) || line.equalsIgnoreCase("quit")) {
            return new Exit(-1);
        }
        if (line.isEmpty()) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        char c = line.charAt(0);
        if (c == 'W' || c == 'w') return new Up(0);
        if (c == 'A' || c == 'a') return new Left(0);
        if (c == 'S' || c == 's') return new Down(0);
        if (c == 'D' || c == 'd') return new Right(0);
        if (c == 'R' || c == 'r') return new Undo(0);
        if (c == 'K' || c == 'k') return new Up(1);
        if (c == 'H' || c == 'h') return new Left(1);
        if (c == 'J' || c == 'j') return new Down(1);
        if (c == 'L' || c == 'l') return new Right(1);
        if (c == 'U' || c == 'u') return new Undo(1);
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }
}

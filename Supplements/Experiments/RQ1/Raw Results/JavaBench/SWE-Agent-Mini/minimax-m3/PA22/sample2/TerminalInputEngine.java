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
        String line = terminalScanner.nextLine();
        if (line == null) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        String trimmed = line.trim();
        if (trimmed.equalsIgnoreCase(StringResources.EXIT_COMMAND_TEXT) || trimmed.equalsIgnoreCase("quit")) {
            return new Exit(-1);
        }
        if (trimmed.isEmpty()) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        // player 0 commands
        switch (trimmed) {
            case "W": case "w":
                return new Up(0);
            case "S": case "s":
                return new Down(0);
            case "A": case "a":
                return new Left(0);
            case "D": case "d":
                return new Right(0);
            case "R": case "r":
                return new Undo(0);
            // player 1 commands
            case "K": case "k":
                return new Up(1);
            case "J": case "j":
                return new Down(1);
            case "H": case "h":
                return new Left(1);
            case "L": case "l":
                return new Right(1);
            case "U": case "u":
                return new Undo(1);
            default:
                return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
    }
}

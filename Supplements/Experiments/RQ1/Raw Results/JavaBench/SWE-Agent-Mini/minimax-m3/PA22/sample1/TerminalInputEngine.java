import java.io.InputStream;
import java.util.Scanner;

public class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine() {
    }

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() {
        if (terminalScanner == null) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        if (!terminalScanner.hasNextLine()) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        String line = terminalScanner.nextLine().trim();
        if (line == null) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        String lower = line.toLowerCase();
        if (lower.equals(StringResources.EXIT_COMMAND_TEXT) || lower.equals("quit")) {
            return new Exit(-1);
        }
        // Player 0: W A S D R
        switch (line) {
            case "W":
            case "w":
                return new Up(0);
            case "A":
            case "a":
                return new Left(0);
            case "S":
            case "s":
                return new Down(0);
            case "D":
            case "d":
                return new Right(0);
            case "R":
            case "r":
                return new Undo(0);
            // Player 1: K H J L U
            case "K":
            case "k":
                return new Up(1);
            case "H":
            case "h":
                return new Left(1);
            case "J":
            case "j":
                return new Down(1);
            case "L":
            case "l":
                return new Right(1);
            case "U":
            case "u":
                return new Undo(1);
            default:
                return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
    }

    public Scanner getTerminalScanner() {
        return this.terminalScanner;
    }

    public void setTerminalScanner(Scanner terminalScanner) {
        this.terminalScanner = terminalScanner;
    }
}

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
        String input = terminalScanner.nextLine().trim().toLowerCase();

        if ("exit".equals(input) || "quit".equals(input)) {
            return new Exit(-1);
        }

        if (input.length() != 1) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }

        char c = input.charAt(0);
        int initiator;
        Action action;

        switch (c) {
            case 'w':
                initiator = 0;
                action = new Up(initiator);
                break;
            case 'a':
                initiator = 0;
                action = new Left(initiator);
                break;
            case 's':
                initiator = 0;
                action = new Down(initiator);
                break;
            case 'd':
                initiator = 0;
                action = new Right(initiator);
                break;
            case 'r':
                initiator = 0;
                action = new Undo(initiator);
                break;
            case 'k':
                initiator = 1;
                action = new Up(initiator);
                break;
            case 'h':
                initiator = 1;
                action = new Left(initiator);
                break;
            case 'j':
                initiator = 1;
                action = new Down(initiator);
                break;
            case 'l':
                initiator = 1;
                action = new Right(initiator);
                break;
            case 'u':
                initiator = 1;
                action = new Undo(initiator);
                break;
            default:
                return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }

        return action;
    }

    public Scanner getTerminalScanner() {
        return terminalScanner;
    }

    public void setTerminalScanner(Scanner terminalScanner) {
        this.terminalScanner = terminalScanner;
    }
}

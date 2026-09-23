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

    @Override
    public Action fetchAction() {
        if (terminalScanner == null || !terminalScanner.hasNextLine()) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        String line = terminalScanner.nextLine().trim();
        if (line.equalsIgnoreCase(StringResources.EXIT_COMMAND_TEXT) || line.equalsIgnoreCase("quit")) {
            return new Exit(-1);
        }
        if (line.isEmpty()) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        char c = Character.toLowerCase(line.charAt(0));
        switch (c) {
            case 'w': return new Up(0);
            case 'a': return new Left(0);
            case 's': return new Down(0);
            case 'd': return new Right(0);
            case 'r': return new Undo(0);
            case 'k': return new Up(1);
            case 'h': return new Left(1);
            case 'j': return new Down(1);
            case 'l': return new Right(1);
            case 'u': return new Undo(1);
            default:
                return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
    }

    public Scanner getTerminalScanner() {
        return terminalScanner;
    }

    public void setTerminalScanner(Scanner terminalScanner) {
        this.terminalScanner = terminalScanner;
    }
}

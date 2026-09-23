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
        if (!terminalScanner.hasNextLine()) {
            return new Exit(-1);
        }
        String input = terminalScanner.nextLine().trim();

        if (input.equalsIgnoreCase(StringResources.EXIT_COMMAND_TEXT) || 
            input.equalsIgnoreCase("quit")) {
            return new Exit(-1);
        }

        if (input.isEmpty()) {
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }

        char c = input.charAt(0);
        int player = 0;

        switch (c) {
            case 'w':
            case 'W':
                return new Up(player);
            case 's':
            case 'S':
                return new Down(player);
            case 'a':
            case 'A':
                return new Left(player);
            case 'd':
            case 'D':
                return new Right(player);
            case 'r':
            case 'R':
                return new Undo(player);
            default:
                return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
    }

    public void setTerminalScanner(Scanner terminalScanner) {
        this.terminalScanner = terminalScanner;
    }

    public Scanner getTerminalScanner() {
        return terminalScanner;
    }
}

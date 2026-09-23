import java.util.Scanner;

public class TerminalInputEngine implements InputEngine {
    private final Scanner terminalScanner;

    public TerminalInputEngine(java.io.InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() {
        if (!terminalScanner.hasNextLine()) {
            return new InvalidInput(-1, "No input");
        }
        final String input = terminalScanner.nextLine().trim();
        return parseInput(input);
    }

    private Action parseInput(String input) {
        if (input == null) {
            return new InvalidInput(-1, "Null input");
        }
        String normalized = input.toLowerCase().trim();
        if (normalized.equals("exit") || normalized.equals("quit")) {
            return new Exit(-1);
        }
        return parseMovementCommand(input);
    }

    private Action parseMovementCommand(String input) {
        if (input.length() != 1) {
            return new InvalidInput(-1, "Invalid input: " + input);
        }
        char cmd = Character.toUpperCase(input.charAt(0));
        switch (cmd) {
            case 'W':
                return new Up(0);
            case 'A':
                return new Left(0);
            case 'S':
                return new Down(0);
            case 'D':
                return new Right(0);
            case 'R':
                return new Undo(0);
            case 'K':
                return new Up(1);
            case 'H':
                return new Left(1);
            case 'J':
                return new Down(1);
            case 'L':
                return new Right(1);
            case 'U':
                return new Undo(1);
            default:
                return new InvalidInput(-1, "Invalid command: " + input);
        }
    }
}

import java.io.InputStream;
import java.util.Scanner;

/**
 * Terminal-based input engine implementation.
 */
public class TerminalInputEngine implements InputEngine {

    private Scanner terminalScanner;

    public TerminalInputEngine() {
        this(System.in);
    }

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    public Action fetchAction() {
        if (terminalScanner.hasNextLine()) {
            String input = terminalScanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()) {
                return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
            }
            // Check for exit command
            if (input.equals(StringResources.EXIT_COMMAND_TEXT)) {
                return new Exit(-1);
            }

            if (input.length() == 1) {
                char command = input.charAt(0);
                // Try to determine player based on command character
                // Player 0: W, A, S, D, R
                // Player 1: K, H, J, L, U
                if (command == 'w') {
                    return new Up(0);
                } else if (command == 's') {
                    return new Down(0);
                } else if (command == 'a') {
                    return new Left(0);
                } else if (command == 'd') {
                    return new Right(0);
                } else if (command == 'r') {
                    return new Undo(0);
                } else if (command == 'k') {
                    return new Up(1);
                } else if (command == 'j') {
                    return new Down(1);
                } else if (command == 'h') {
                    return new Left(1);
                } else if (command == 'l') {
                    return new Right(1);
                } else if (command == 'u') {
                    return new Undo(1);
                }
            }

            // Check for other exit-like commands
            if (input.equals("quit")) {
                return new Exit(-1);
            }

            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }
}

import java.io.InputStream;
import java.util.Scanner;

class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    public TerminalInputEngine() {
    }

    public Action fetchAction() {
        if (terminalScanner.hasNextLine()) {
            String line = terminalScanner.nextLine().trim();
            if (line.isEmpty()) {
                return fetchAction();
            }
            String lower = line.toLowerCase();
            if (lower.equals(StringResources.EXIT_COMMAND_TEXT)) {
                return new Exit(-1);
            }
            if (line.length() == 1) {
                char c = line.charAt(0);
                if (c == 'w') {
                    return new Up(0);
                }
                if (c == 'a') {
                    return new Left(0);
                }
                if (c == 's') {
                    return new Down(0);
                }
                if (c == 'd') {
                    return new Right(0);
                }
                if (c == 'r') {
                    return new Undo(0);
                }
                if (c == 'k') {
                    return new Up(1);
                }
                if (c == 'h') {
                    return new Left(1);
                }
                if (c == 'j') {
                    return new Down(1);
                }
                if (c == 'l') {
                    return new Right(1);
                }
                if (c == 'u') {
                    return new Undo(1);
                }
                return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
            }
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }

    public Scanner getTerminalScanner() {
        return terminalScanner;
    }

    public void setTerminalScanner(Scanner terminalScanner) {
        this.terminalScanner = terminalScanner;
    }
}

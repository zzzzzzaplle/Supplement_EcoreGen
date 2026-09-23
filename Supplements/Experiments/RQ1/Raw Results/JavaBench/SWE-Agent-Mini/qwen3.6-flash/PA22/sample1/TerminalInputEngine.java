import java.io.InputStream;
import java.util.Scanner;

public class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;
    private InputStream terminalStream;

    public TerminalInputEngine() {
    }

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalStream = terminalStream;
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() {
        if (terminalScanner.hasNextLine()) {
            String line = terminalScanner.nextLine().trim();
            if (line.equalsIgnoreCase(StringResources.EXIT_COMMAND_TEXT) || line.equalsIgnoreCase("quit")) {
                return new Exit(-1);
            }
            if (line.trim().isEmpty()) {
                return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
            }
            if (line.length() == 1) {
                char c = line.charAt(0);
                if (c == 'w' || c == 'W') return new Up(0);
                if (c == 'a' || c == 'A') return new Left(0);
                if (c == 's' || c == 'S') return new Down(0);
                if (c == 'd' || c == 'D') return new Right(0);
                if (c == 'r' || c == 'R') return new Undo(0);
                if (c == 'k' || c == 'K') return new Up(1);
                if (c == 'h' || c == 'H') return new Left(1);
                if (c == 'j' || c == 'J') return new Down(1);
                if (c == 'l' || c == 'L') return new Right(1);
                if (c == 'u' || c == 'U') return new Undo(1);
            }
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }
}

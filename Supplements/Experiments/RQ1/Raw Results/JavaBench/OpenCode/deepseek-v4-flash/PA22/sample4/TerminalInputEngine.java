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
        if (input.equals("exit") || input.equals("quit")) {
            return new Exit(-1);
        }
        if (input.length() == 1) {
            char c = input.charAt(0);
            int initiator;
            Move move = null;
            switch (c) {
                case 'W': initiator = 0; move = new Up(initiator); break;
                case 'A': initiator = 0; move = new Left(initiator); break;
                case 'S': initiator = 0; move = new Down(initiator); break;
                case 'D': initiator = 0; move = new Right(initiator); break;
                case 'R': return new Undo(0);
                case 'K': initiator = 1; move = new Up(initiator); break;
                case 'H': initiator = 1; move = new Left(initiator); break;
                case 'J': initiator = 1; move = new Down(initiator); break;
                case 'L': initiator = 1; move = new Right(initiator); break;
                case 'U': return new Undo(1);
                default: return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
            }
            if (move != null) {
                return move;
            }
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

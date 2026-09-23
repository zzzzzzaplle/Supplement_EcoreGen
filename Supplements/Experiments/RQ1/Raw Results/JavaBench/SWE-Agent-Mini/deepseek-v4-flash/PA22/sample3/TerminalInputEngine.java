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
        
        if (input.length() == 1) {
            char ch = input.charAt(0);
            // Player 0: W, A, S, D, R
            if (ch == 'w') return new Up(0);
            if (ch == 'a') return new Left(0);
            if (ch == 's') return new Down(0);
            if (ch == 'd') return new Right(0);
            if (ch == 'r') return new Undo(0);
            // Player 1: K, H, J, L, U
            if (ch == 'k') return new Up(1);
            if (ch == 'h') return new Left(1);
            if (ch == 'j') return new Down(1);
            if (ch == 'l') return new Right(1);
            if (ch == 'u') return new Undo(1);
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

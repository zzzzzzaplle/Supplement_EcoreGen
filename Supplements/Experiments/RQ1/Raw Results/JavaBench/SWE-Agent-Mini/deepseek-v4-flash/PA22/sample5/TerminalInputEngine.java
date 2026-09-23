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
        String input = terminalScanner.nextLine().trim();
        
        if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
            return new Exit(-1);
        }
        
        // Player 0 controls: W (up), A (left), S (down), D (right), R (undo)
        if (input.equalsIgnoreCase("W")) {
            return new Up(0);
        } else if (input.equalsIgnoreCase("A")) {
            return new Left(0);
        } else if (input.equalsIgnoreCase("S")) {
            return new Down(0);
        } else if (input.equalsIgnoreCase("D")) {
            return new Right(0);
        } else if (input.equalsIgnoreCase("R")) {
            return new Undo(0);
        }
        
        // Player 1 controls: K (up), H (left), J (down), L (right), U (undo)
        if (input.equalsIgnoreCase("K")) {
            return new Up(1);
        } else if (input.equalsIgnoreCase("H")) {
            return new Left(1);
        } else if (input.equalsIgnoreCase("J")) {
            return new Down(1);
        } else if (input.equalsIgnoreCase("L")) {
            return new Right(1);
        } else if (input.equalsIgnoreCase("U")) {
            return new Undo(1);
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

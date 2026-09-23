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

    public Scanner getTerminalScanner() {
        return this.terminalScanner;
    }

    public void setTerminalScanner(Scanner terminalScanner) {
        this.terminalScanner = terminalScanner;
    }

    @Override
    public Action fetchAction() {
        if (!this.terminalScanner.hasNextLine()) {
            return new Exit(-1);
        }
        String input = this.terminalScanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
            return new Exit(-1);
        }
        if (input.isEmpty()) {
            return new InvalidInput(-1, "Empty input");
        }
        // Player 0: W(up), A(left), S(down), D(right), R(undo)
        switch (input.toUpperCase()) {
            case "W":
                return new Up(0);
            case "A":
                return new Left(0);
            case "S":
                return new Down(0);
            case "D":
                return new Right(0);
            case "R":
                return new Undo(0);
            // Player 1: K(up), H(left), J(down), L(right), U(undo)
            case "K":
                return new Up(1);
            case "H":
                return new Left(1);
            case "J":
                return new Down(1);
            case "L":
                return new Right(1);
            case "U":
                return new Undo(1);
            default:
                return new InvalidInput(-1, "Unknown command: " + input);
        }
    }
}

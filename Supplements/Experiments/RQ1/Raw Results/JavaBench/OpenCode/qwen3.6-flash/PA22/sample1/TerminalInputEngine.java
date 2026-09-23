import java.io.InputStream;
import java.util.Scanner;

class TerminalInputEngine implements InputEngine {

    private Scanner terminalScanner;

    public TerminalInputEngine() {
        this(System.in);
    }

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    @Override
    public Action fetchAction() {
        if (this.terminalScanner.hasNextLine()) {
            String input = this.terminalScanner.nextLine().trim();

            // Check for exit/quit commands
            if (StringResources.EXIT_COMMAND_TEXT.equals(input) || "quit".equals(input)) {
                return new Exit(-1);
            }

            // Check for undo commands per player
            if (input.equals("R")) {
                return new Undo(0);
            }
            if (input.equals("U")) {
                return new Undo(1);
            }

            // Player 0 control: W, A, S, D
            if (input.equals("W")) return new Up(0);
            if (input.equals("A")) return new Left(0);
            if (input.equals("S")) return new Down(0);
            if (input.equals("D")) return new Right(0);

            // Player 1 control: K, H, J, L
            if (input.equals("K")) return new Up(1);
            if (input.equals("H")) return new Left(1);
            if (input.equals("J")) return new Down(1);
            if (input.equals("L")) return new Right(1);

            // Invalid input
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }
}

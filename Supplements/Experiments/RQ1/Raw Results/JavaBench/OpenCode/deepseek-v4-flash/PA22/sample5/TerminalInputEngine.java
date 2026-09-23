import java.io.InputStream;
import java.util.Scanner;

public class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    public TerminalInputEngine() {
    }

    @Override
    public Action fetchAction() {
        String input = terminalScanner.nextLine().trim().toLowerCase();
        if (input.equals("exit") || input.equals("quit")) {
            return new Exit(-1);
        }
        if (input.equals("w")) {
            return new Up(0);
        } else if (input.equals("a")) {
            return new Left(0);
        } else if (input.equals("s")) {
            return new Down(0);
        } else if (input.equals("d")) {
            return new Right(0);
        } else if (input.equals("r")) {
            return new Undo(0);
        } else if (input.equals("k")) {
            return new Up(1);
        } else if (input.equals("h")) {
            return new Left(1);
        } else if (input.equals("j")) {
            return new Down(1);
        } else if (input.equals("l")) {
            return new Right(1);
        } else if (input.equals("u")) {
            return new Undo(1);
        }
        return new InvalidInput(-1, input);
    }
}

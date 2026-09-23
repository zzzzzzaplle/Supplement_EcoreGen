import java.io.InputStream;
import java.util.Scanner;

public class TerminalInputEngine implements InputEngine {
    private Scanner terminalScanner;

    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    public Action fetchAction() {
        if (terminalScanner.hasNextLine()) {
            String input = terminalScanner.nextLine().trim();
            
            if (input.equalsIgnoreCase(StringResources.EXIT_COMMAND_TEXT)) {
                return new Exit(-1);
            }
            
            if (input.length() == 1) {
                char key = input.charAt(0);
                if (isPlayer0Key(key)) {
                    return getPlayer0Action(key);
                } else if (isPlayer1Key(key)) {
                    return getPlayer1Action(key);
                }
            }
            
            return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
        }
        return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);
    }
    
    private boolean isPlayer0Key(char key) {
        return key == 'w' || key == 'a' || key == 's' || key == 'd' || key == 'r';
    }
    
    private boolean isPlayer1Key(char key) {
        return key == 'k' || key == 'h' || key == 'j' || key == 'l' || key == 'u';
    }
    
    private Action getPlayer0Action(char key) {
        switch (key) {
            case 'w': return new Up(0);
            case 'a': return new Left(0);
            case 's': return new Down(0);
            case 'd': return new Right(0);
            case 'r': return new Undo(0);
            default: return new InvalidInput(0, StringResources.INVALID_INPUT_MESSAGE);
        }
    }
    
    private Action getPlayer1Action(char key) {
        switch (key) {
            case 'k': return new Up(1);
            case 'h': return new Left(1);
            case 'j': return new Down(1);
            case 'l': return new Right(1);
            case 'u': return new Undo(1);
            default: return new InvalidInput(1, StringResources.INVALID_INPUT_MESSAGE);
        }
    }
}

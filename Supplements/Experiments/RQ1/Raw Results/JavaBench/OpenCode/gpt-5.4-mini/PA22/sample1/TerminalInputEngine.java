public class TerminalInputEngine implements InputEngine {
    private java.util.Scanner terminalScanner;

    public TerminalInputEngine() {
    }

    public TerminalInputEngine(java.io.InputStream terminalStream) {
        this.terminalScanner = new java.util.Scanner(terminalStream);
    }

    public Action fetchAction() {
        return null;
    }

    public java.util.Scanner getTerminalScanner() {
        return terminalScanner;
    }

    public void setTerminalScanner(java.util.Scanner terminalScanner) {
        this.terminalScanner = terminalScanner;
    }
}

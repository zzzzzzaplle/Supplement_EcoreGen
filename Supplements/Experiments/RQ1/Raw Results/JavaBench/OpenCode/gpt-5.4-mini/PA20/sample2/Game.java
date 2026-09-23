public abstract class Game implements Cloneable {
    protected Configuration configuration;
    protected Piece[][] board;
    protected Player currentPlayer;
    protected int numMoves;

    public Game() {
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Piece[][] getBoard() {
        return this.board;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getNumMoves() {
        return this.numMoves;
    }

    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    public Piece getPiece(Place place) {
        return this.board[place.getX()][place.getY()];
    }

    public Piece getPiece(int x, int y) {
        Place place = new Place();
        place.setX(x);
        place.setY(y);
        return this.getPiece(place);
    }

    public void refreshOutput() {
        int size = this.configuration.getSize();
        java.util.ArrayList<java.util.List<String>> contents = new java.util.ArrayList<java.util.List<String>>();
        for (int row = size - 1; row >= 0; row--) {
            java.util.ArrayList<String> rowContent = new java.util.ArrayList<String>();
            for (int col = 0; col < size; col++) {
                Piece piece = this.getPiece(col, row);
                if (piece == null) {
                    if (this.getCentralPlace().equals(new Place(col, row))) {
                        rowContent.add("x");
                    } else {
                        rowContent.add(".");
                    }
                } else {
                    Player player = piece.getPlayer();
                    rowContent.add(String.format("%s%c%s", player.getColor(), piece.getLabel(), Color.DEFAULT));
                }
            }
            contents.add(rowContent);
        }
        java.util.ArrayList<String> xCoordinates = new java.util.ArrayList<String>();
        for (int i = 0; i < size; i++) {
            xCoordinates.add(String.valueOf((char) ('a' + i)));
        }
        java.util.Collections.reverse(contents);
        System.out.print("\u001b[2J");
        System.out.flush();
    }

    public Place getCentralPlace() {
        return this.configuration.getCentralPlace();
    }

    public abstract Player start();
    public abstract Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove);
    public abstract void updateScore(Player player, Piece piece, Move move);
    public abstract void movePiece(Move move);
    public abstract Move[] getAvailableMoves(Player player);

    @Override
    public Game clone() throws CloneNotSupportedException {
        Game cloned = (Game) super.clone();
        cloned.configuration = this.configuration.clone();
        cloned.board = this.board.clone();
        for (int i = 0; i < this.configuration.getSize(); i++) {
            cloned.board[i] = this.board[i].clone();
            if (this.configuration.getSize() >= 0)
                System.arraycopy(this.board[i], 0, cloned.board[i], 0, this.configuration.getSize());
        }
        cloned.currentPlayer = currentPlayer == null ? null : currentPlayer.clone();
        return cloned;
    }
}

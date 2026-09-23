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

    public abstract Player start();
    public abstract Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove);
    public abstract void updateScore(Player player, Piece piece, Move move);
    public abstract void movePiece(Move move);
    public abstract Move[] getAvailableMoves(Player player);

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
        System.out.println();
        System.out.println("### COMP3021 Programming Assignment 1 ###");
        System.out.println();
        System.out.println("Guide: to move a piece, input the coordinate of source and the destination.");
        System.out.println("For example: a1->b2 means to move the piece at 'a1' to 'b2'");
        if (this.numMoves < this.configuration.getNumMovesProtection()) {
            System.out.println();
            System.out.println("Notice: first " + this.configuration.getNumMovesProtection() + " moves are not allowed to capture pieces or win the game.");
        }
        System.out.println();
        System.out.println("Total Moves: " + this.numMoves);
        for (Player player : this.configuration.getPlayers()) {
            System.out.printf("%s%s%s score: %d\n", player.getColor(), player.getName(), Color.DEFAULT, player.getScore());
        }
        System.out.println();
        int leftPadding = 8;
        StringBuilder paddingSpaceBuilder = new StringBuilder();
        paddingSpaceBuilder.append(" ".repeat(leftPadding));
        System.out.printf("%s%s\n", paddingSpaceBuilder.toString(), xCoordinates.parallelStream().collect(java.util.stream.Collectors.joining(" ")));
        StringBuilder borderBuilder = new StringBuilder();
        borderBuilder.append("-".repeat(Math.max(0, contents.get(0).size() * 2 - 1)));
        System.out.printf("%s%s\n", paddingSpaceBuilder.toString(), borderBuilder.toString());
        for (int row = contents.size() - 1; row >= 0; row--) {
            System.out.printf("%" + (leftPadding - 1) + "d|%s|%d\n", row + 1, contents.get(row).parallelStream().map(Object::toString).collect(java.util.stream.Collectors.joining(" ")), row + 1);
        }
        System.out.printf("%s%s\n", paddingSpaceBuilder.toString(), borderBuilder.toString());
        System.out.printf("%s%s\n", paddingSpaceBuilder.toString(), xCoordinates.parallelStream().collect(java.util.stream.Collectors.joining(" ")));
        System.out.println();
    }

    public Place getCentralPlace() {
        return this.configuration.getCentralPlace();
    }

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

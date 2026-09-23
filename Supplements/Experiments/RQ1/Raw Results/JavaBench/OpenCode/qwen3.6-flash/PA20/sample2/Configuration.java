import java.lang.CloneNotSupportedException;

public class Configuration implements Cloneable {
    private int size;
    private java.util.List<Player> players;
    private java.util.List<java.util.List<Piece>> initialBoard;
    private Place centralPlace;
    private int numMovesProtection;

    public Configuration() {
    }

    public Configuration(int size, Player[] players, int numMovesProtection) {
        if (size < 3) {
            throw new InvalidConfigurationError("size of gameboard must be at least 3");
        }
        if (size % 2 != 1) {
            throw new InvalidConfigurationError("size of gameboard must be an odd number");
        }
        if (size > 25) {
            throw new InvalidConfigurationError("size of gameboard is at most 25");
        }
        this.size = size;
        this.players = new java.util.ArrayList<>();
        for (Player p : players) {
            this.players.add(p);
        }
        if (players.length != 2) {
            throw new InvalidConfigurationError("there must be exactly two players");
        }
        this.initialBoard = new java.util.ArrayList<>();
        for (int x = 0; x < size; x++) {
            java.util.List<Piece> row = new java.util.ArrayList<>();
            for (int y = 0; y < size; y++) {
                row.add(null);
            }
            this.initialBoard.add(row);
        }
        this.centralPlace = new Place(size / 2, size / 2);

        if (numMovesProtection < 0) {
            throw new InvalidConfigurationError("number of moves with capture protection cannot be negative");
        }
        this.numMovesProtection = numMovesProtection;
    }

    public Configuration(int size, Player[] players) {
        this(size, players, 0);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public java.util.List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(java.util.List<Player> players) {
        this.players = players;
    }

    public java.util.List<java.util.List<Piece>> getInitialBoard() {
        return initialBoard;
    }

    public void setInitialBoard(java.util.List<java.util.List<Piece>> initialBoard) {
        this.initialBoard = initialBoard;
    }

    public Place getCentralPlace() {
        return centralPlace;
    }

    public void setCentralPlace(Place centralPlace) {
        this.centralPlace = centralPlace;
    }

    public int getNumMovesProtection() {
        return numMovesProtection;
    }

    public void setNumMovesProtection(int numMovesProtection) {
        this.numMovesProtection = numMovesProtection;
    }

    public void addInitialPiece(Piece piece, Place place) {
        if (!piece.getPlayer().equals(this.players.get(0)) && !piece.getPlayer().equals(this.players.get(1))) {
            throw new InvalidConfigurationError("the player of the piece is unknown");
        }
        if (place.x() >= this.size || place.y() >= this.size) {
            throw new InvalidConfigurationError("the place" + place.toString() + " must be inside the gameboard");
        }
        if (place.equals(this.centralPlace)) {
            throw new InvalidConfigurationError("piece cannot be put at central place initially");
        }
        this.initialBoard.get(place.x()).set(place.y(), piece);
    }

    public void addInitialPiece(Piece piece, int x, int y) {
        this.addInitialPiece(piece, new Place(x, y));
    }

    public Configuration clone() throws CloneNotSupportedException {
        Configuration cloned = (Configuration) super.clone();
        cloned.players = new java.util.ArrayList<>();
        for (Player p : this.players) {
            cloned.players.add(p.clone());
        }
        cloned.initialBoard = new java.util.ArrayList<>();
        for (int i = 0; i < this.size; i++) {
            cloned.initialBoard.add(new java.util.ArrayList<>());
            for (int j = 0; j < this.size; j++) {
                cloned.initialBoard.get(i).add(null);
            }
        }
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                cloned.initialBoard.get(i).set(j, this.initialBoard.get(i).get(j));
            }
        }
        cloned.centralPlace = this.centralPlace.clone();
        return cloned;
    }
}

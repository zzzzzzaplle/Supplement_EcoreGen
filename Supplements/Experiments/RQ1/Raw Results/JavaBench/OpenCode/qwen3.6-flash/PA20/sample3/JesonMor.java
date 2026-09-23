import java.util.HashSet;
import java.util.Set;

class JesonMor extends Game implements Cloneable {
    private Rule[] rules;

    public JesonMor() {}

    public JesonMor(Configuration configuration) {
        this.configuration = configuration;
        this.numMoves = 0;
        this.configuration.getCentralPlace();
        this.board = new Piece[this.configuration.getSize()][];
        for (int i = 0; i < this.configuration.getSize(); i++) {
            this.board[i] = new Piece[this.configuration.getSize()];
            java.util.Arrays.fill(this.board[i], null);
        }
        initializeBoard();
        this.currentPlayer = this.configuration.getPlayers()[0];
        this.rules = new Rule[]{
                new VacantRule(),
                new NilMoveRule(),
                new OutOfBoundaryRule(),
                new OccupiedRule(),
                new FirstNMovesProtectionRule(this.configuration.getNumMovesProtection()),
                new KnightMoveRule(),
                new KnightBlockRule(),
                new ArcherMoveRule()
        };
    }

    private void initializeBoard() {
        Piece[][] initialBoard = this.configuration.getInitialBoard();
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (initialBoard[x][y] != null) {
                    this.board[x][y] = initialBoard[x][y];
                }
            }
        }
    }

    public Player start() {
        while (true) {
            this.refreshOutput();
            Move[] availableMoves = getAvailableMoves(this.currentPlayer);

            // if the current player has no available moves, the game ends
            if (availableMoves == null || availableMoves.length == 0) {
                Player winner = determineWinnerByScore();
                this.refreshOutput();
                System.out.println("Player " + winner.getName() + " wins by score!");
                return winner;
            }

            Move selectedMove = this.currentPlayer.nextMove(this, availableMoves);
            if (selectedMove == null) {
                this.refreshOutput();
                System.out.println("Invalid move, try again!");
                continue;
            }

            // validate selected move with all applicable rules
            if (isMoveValid(selectedMove)) {
                this.movePiece(selectedMove);
                this.updateScore(this.currentPlayer,
                        this.board[selectedMove.getDestination().x()][selectedMove.getDestination().y()],
                        selectedMove);
                this.numMoves++;
                this.refreshOutput();

                // check win condition
                Piece pieceAtDestination = this.board[selectedMove.getDestination().x()][selectedMove.getDestination().y()];
                Player winner = getWinner(this.currentPlayer, pieceAtDestination, selectedMove);
                if (winner != null) {
                    System.out.println("Player " + winner.getName() + " wins!");
                    return winner;
                }

                // switch players
                this.currentPlayer = this.configuration.getPlayers()[(this.numMoves - 1) % 2 == 0 ? 1 : 0];
            } else {
                this.refreshOutput();
                System.out.println("Invalid move: " + getViolationMessage(selectedMove));
            }
        }
    }

    private boolean isMoveValid(Move move) {
        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                return false;
            }
        }
        return true;
    }

    private String getViolationMessage(Move move) {
        for (Rule rule : rules) {
            if (!rule.validate(this, move)) {
                return rule.getDescription();
            }
        }
        return "Unknown rule violated";
    }

    @Override
    public Player getWinner(Player lastPlayer, Piece lastPiece, Move lastMove) {
        // protection phase: no winner
        if (this.numMoves <= this.configuration.getNumMovesProtection()) {
            return null;
        }

        // win condition 1: Knight leaves central place
        if (lastPiece instanceof Knight) {
            if (lastMove.getSource().equals(this.configuration.getCentralPlace())
                    && !lastMove.getDestination().equals(this.configuration.getCentralPlace())) {
                return lastPlayer;
            }
        }

        // win condition 2: only one player's pieces remain
        int player1PieceCount = 0;
        int player2PieceCount = 0;
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.board[x][y];
                if (piece != null) {
                    if (piece.getPlayer().equals(this.configuration.getPlayers()[0])) {
                        player1PieceCount++;
                    } else if (piece.getPlayer().equals(this.configuration.getPlayers()[1])) {
                        player2PieceCount++;
                    }
                }
            }
        }
        if (player1PieceCount == 0 && player2PieceCount > 0) {
            return this.configuration.getPlayers()[1];
        }
        if (player2PieceCount == 0 && player1PieceCount > 0) {
            return this.configuration.getPlayers()[0];
        }
        if (player1PieceCount == 0 && player2PieceCount == 0) {
            return determineWinnerByScore();
        }
        return null;
    }

    private Player determineWinnerByScore() {
        Player player1 = this.configuration.getPlayers()[0];
        Player player2 = this.configuration.getPlayers()[1];
        if (player1.getScore() > player2.getScore()) {
            return player1;
        } else if (player2.getScore() > player1.getScore()) {
            return player2;
        } else {
            // tie, current player wins
            return this.currentPlayer;
        }
    }

    @Override
    public void updateScore(Player player, Piece piece, Move move) {
        int distance = Math.abs(move.getDestination().x() - move.getSource().x())
                + Math.abs(move.getDestination().y() - move.getSource().y());
        player.setScore(player.getScore() + distance);
    }

    @Override
    public void movePiece(Move move) {
        Piece piece = this.board[move.getSource().x()][move.getSource().y()];
        this.board[move.getSource().x()][move.getSource().y()] = null;
        this.board[move.getDestination().x()][move.getDestination().y()] = piece;
    }

    @Override
    public Move[] getAvailableMoves(Player player) {
        Set<Move> allMoves = new HashSet<>();
        int size = this.configuration.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Piece piece = this.board[x][y];
                if (piece != null && piece.getPlayer().equals(player)) {
                    Place source = new Place(x, y);
                    Move[] pieceMoves = piece.getAvailableMoves(this, source);
                    for (Move m : pieceMoves) {
                        if (isMoveValid(m)) {
                            allMoves.add(m);
                        }
                    }
                }
            }
        }
        return allMoves.toArray(new Move[0]);
    }

    @Override
    public JesonMor clone() throws CloneNotSupportedException {
        JesonMor cloned = (JesonMor) super.clone();
        cloned.rules = this.rules.clone();
        return cloned;
    }

    public Rule[] getRules() {
        return rules;
    }

    public void setRules(Rule[] rules) {
        this.rules = rules;
    }
}

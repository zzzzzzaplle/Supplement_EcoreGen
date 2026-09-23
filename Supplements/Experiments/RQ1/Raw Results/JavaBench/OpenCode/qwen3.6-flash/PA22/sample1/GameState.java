import java.util.*;
import java.util.stream.Collectors;

class GameState {

    private Map<Position, Entity> board;
    private Set<Position> destinations;
    private int boardWidth;
    private int boardHeight;
    private int undoQuota;
    private boolean undoUnlimited;
    private List<GameState> history = new ArrayList<>();
    private List<Stack<GameStateTransition.Transition>> checkpointHistory = new ArrayList<>();

    public GameState(GameMap gameMap) {
        this.board = new HashMap<>(gameMap.getMap());
        this.boardWidth = gameMap.getMaxWidth();
        this.boardHeight = gameMap.getMaxHeight();
        this.destinations = new HashSet<>(gameMap.getDestinations());
        int limit = gameMap.getUndoLimit().orElse(-1);
        if (limit == 0) {
            this.undoQuota = 0;
            this.undoUnlimited = false;
        } else if (limit < 0) {
            this.undoUnlimited = true;
        } else {
            this.undoQuota = limit;
            this.undoUnlimited = false;
        }
    }

    private GameState(
            Map<Position, Entity> board,
            Set<Position> destinations,
            int boardWidth,
            int boardHeight,
            int undoQuota,
            boolean undoUnlimited,
            List<GameState> history,
            List<Stack<GameStateTransition.Transition>> checkpointHistory
    ) {
        this.board = new HashMap<>(board);
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.destinations = destinations;
        this.undoQuota = undoQuota;
        this.undoUnlimited = undoUnlimited;
        this.history = new ArrayList<>(history);
        this.checkpointHistory = new ArrayList<>();
        for (Stack<GameStateTransition.Transition> stack : checkpointHistory) {
            this.checkpointHistory.add(new Stack<>(stack));
        }
    }

    public Position getPlayerPositionById(int id) {
        for (Map.Entry<Position, Entity> entry : this.board.entrySet()) {
            if (entry.getValue() instanceof Player && ((Player) entry.getValue()).getId() == id) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException(StringResources.PLAYER_NOT_FOUND);
    }

    public Set<Position> getAllPlayerPositions() {
        Set<Position> positions = new HashSet<>();
        for (Map.Entry<Position, Entity> entry : this.board.entrySet()) {
            if (entry.getValue() instanceof Player) {
                positions.add(entry.getKey());
            }
        }
        return positions;
    }

    public Entity getEntity(Position position) {
        return this.board.getOrDefault(position, new Empty());
    }

    public Set<Position> getDestinations() {
        return this.destinations;
    }

    public boolean isWin() {
        // Check all destinations have boxes
        return this.board.entrySet().stream()
                .filter(e -> e.getValue() instanceof Box)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet())
                .containsAll(this.destinations);
    }

    public void move(Position from, Position to) {
        Entity toEntity = this.board.getOrDefault(to, new Empty());  
        Entity fromEntity = this.board.get(from);

        // Save current state for undo
        GameState previousState = new GameState(
                new HashMap<>(this.board),
                new HashSet<>(this.destinations),
                this.boardWidth,
                this.boardHeight,
                this.undoQuota,
                this.undoUnlimited,
                new ArrayList<>(this.history),
                new ArrayList<>()
        );
        for (Stack<GameStateTransition.Transition> stack : this.checkpointHistory) {
            previousState.checkpointHistory.add(new Stack<>(stack));
        }
        this.history.add(previousState);

        boolean boxMoved = false;

        if (fromEntity instanceof Player) {
            Player player = (Player) fromEntity;
            if (toEntity instanceof Box) {
                Box box = (Box) toEntity;
                if (box.getPlayerId() == player.getId()) {
                    // Push the box
                    Position boxBehind = nextPosition(to);
                    Entity behindEntity = this.board.getOrDefault(boxBehind, new Empty());
                    if (behindEntity instanceof Empty || behindEntity instanceof Wall) {
                        // If destination, change empty to empty (destinations not shown separately)
                        if (behindEntity instanceof Empty) {
                            this.board.put(boxBehind, box);
                            this.board.remove(to);
                            // Check if box was pushed to destination
                            if (this.destinations.contains(boxBehind)) {
                                boxMoved = true;
                            }
                        } else {
                            this.board.put(from, new Empty());
                        }
                    } else {
                        // Can't push - put back and throw
                        throw new IllegalStateException("Cannot push box into wall or other entity");
                    }
                } else {
                    // Box doesn't belong to this player
                    throw new IllegalStateException("Box doesn't belong to this player");
                }
            } else if (toEntity instanceof Empty) {
                this.board.put(to, player);
                this.board.remove(from);
            } else {
                throw new IllegalStateException("Cannot move to " + toEntity.getClass().getSimpleName());
            }
        } else {
            throw new IllegalStateException("From position doesn't have a player");
        }

        if (boxMoved) {
            Stack<GameStateTransition.Transition> stack = new Stack<>();
            GameStateTransition.Transition transition = new GameStateTransition.Transition();
            transition.add(from, to);
            stack.push(transition);
            this.checkpointHistory.add(stack);
        }
    }

    private Position nextPosition(Position pos) {
        // Calculate adjacent position below
        return Position.of(pos.x(), pos.y() + 1);
    }

    public void checkpoint() {
        // Already handled in move()
    }

    public void undo() {
        if (this.history.isEmpty()) {
            throw new IllegalStateException("Nothing to undo");
        }
        if (!this.undoUnlimited && this.undoQuota <= 0) {
            throw new IllegalStateException("No undo quota remaining");
        }

        // Consume quota only if there's a checkpoint to undo
        boolean hasCheckpoint = !this.checkpointHistory.isEmpty();
        if (hasCheckpoint && !this.undoUnlimited) {
            this.undoQuota--;
        }

        GameState previousState = this.history.remove(this.history.size() - 1);
        // Merge checkpoint data
        if (!this.checkpointHistory.isEmpty()) {
            Stack<GameStateTransition.Transition> top = this.checkpointHistory.remove(this.checkpointHistory.size() - 1);
            for (GameStateTransition.Transition t : top) {
                previousState.checkpointHistory.add(new Stack<>());
                previousState.checkpointHistory.get(previousState.checkpointHistory.size() - 1).add(t);
            }
        }

        this.board = new HashMap<>(previousState.board);
        this.history = previousState.history;
        this.undoQuota = previousState.undoQuota;
        this.undoUnlimited = previousState.undoUnlimited;
    }

    public int getMapMaxWidth() {
        return this.boardWidth;
    }

    public int getMapMaxHeight() {
        return this.boardHeight;
    }

    public Map<Position, Entity> getBoard() {
        return this.board;
    }
}

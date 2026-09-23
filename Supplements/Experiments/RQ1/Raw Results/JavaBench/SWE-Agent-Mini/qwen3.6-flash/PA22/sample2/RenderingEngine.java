
/**
 * Interface for rendering engines.
 */
public interface RenderingEngine {
    /**
     * Render the game state.
     * @param state the game state
     */
    void render(GameState state);

    /**
     * Display a message.
     * @param content the message content
     */
    void message(String content);
}

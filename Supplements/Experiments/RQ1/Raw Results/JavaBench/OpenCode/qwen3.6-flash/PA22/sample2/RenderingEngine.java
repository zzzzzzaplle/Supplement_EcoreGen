/**
 * Interface for rendering engines.
 */
public interface RenderingEngine {

    void render(GameState state);

    void message(String content);
}

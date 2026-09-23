public interface Rule {
    boolean validate(Game game, Move move);
    String getDescription();
}

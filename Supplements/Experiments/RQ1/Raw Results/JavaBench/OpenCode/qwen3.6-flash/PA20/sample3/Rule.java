interface Rule {
    public boolean validate(Game game, Move move);
    public String getDescription();
}

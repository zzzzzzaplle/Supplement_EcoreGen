public class Dictionary extends Volume {
    private String dictionaryId;

    public Dictionary() {
    }

    public Dictionary(String title, String author, String dictionaryId) {
        super(title);
        setAuthor(author);
        this.dictionaryId = dictionaryId;
    }

    public String getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(String dictionaryId) {
        this.dictionaryId = dictionaryId;
    }
}

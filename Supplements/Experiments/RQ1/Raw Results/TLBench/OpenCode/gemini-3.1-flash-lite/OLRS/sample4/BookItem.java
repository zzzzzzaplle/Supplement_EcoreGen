public class BookItem extends LibraryItem {
    private BookItemType type;

    public BookItem() {}

    public BookItemType getType() { return type; }
    public void setType(BookItemType type) { this.type = type; }
}

public class BookItem extends LibraryItem {
    private BookItemType type;

    public BookItem() {
        super();
        this.type = BookItemType.EBOOK;
    }

    public BookItemType getType() {
        return type;
    }

    public void setType(BookItemType type) {
        this.type = type;
    }
}

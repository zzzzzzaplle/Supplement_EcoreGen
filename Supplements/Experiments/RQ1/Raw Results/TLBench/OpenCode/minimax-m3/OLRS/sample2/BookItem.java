public class BookItem extends LibraryItem {

    private BookItemType type;

    public BookItem() {
        super();
    }

    public BookItemType getType() {
        return type;
    }

    public void setType(BookItemType type) {
        this.type = type;
    }
}

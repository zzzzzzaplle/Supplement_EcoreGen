public class ItemLine {
    private Integer quantity;
    private LibraryItem libraryItem;

    public ItemLine() {
        this.quantity = 1;
        this.libraryItem = null;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LibraryItem getLibraryItem() {
        return libraryItem;
    }

    public void setLibraryItem(LibraryItem libraryItem) {
        this.libraryItem = libraryItem;
    }
}

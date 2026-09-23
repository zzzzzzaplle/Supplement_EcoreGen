public abstract class LibraryItem {
    private LibraryItemStatus status;

    public LibraryItem() {
        this.status = LibraryItemStatus.AVAILABLE;
    }

    public LibraryItemStatus getStatus() {
        return status;
    }

    public void setStatus(LibraryItemStatus status) {
        this.status = status;
    }
}

import java.util.ArrayList;
import java.util.List;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.itemLines = new ArrayList<>();
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<ItemLine> getItemLines() {
        return itemLines;
    }

    public void setItemLines(List<ItemLine> itemLines) {
        this.itemLines = itemLines;
    }

    // Helper to check if a LibraryItem is print format book or disc digital item
    private boolean isPrintBookOrDiscDigital(LibraryItem libraryItem) {
        if (libraryItem instanceof BookItem) {
            BookItem bookItem = (BookItem) libraryItem;
            return bookItem.getType() == BookItemType.PRINT_FORMAT;
        }
        if (libraryItem instanceof DigitalItem) {
            DigitalItem digitalItem = (DigitalItem) libraryItem;
            return digitalItem.getOption() == DigitalItemOption.DISC;
        }
        return false;
    }

    public boolean addItemLine(ItemLine itemLine) {
        // Precondition: Order must be in Pending status
        if (this.status != OrderStatus.PENDING) {
            return false;
        }

        // Check for duplicate item line (same library item)
        if (this.itemLines != null) {
            for (ItemLine existingLine : this.itemLines) {
                if (existingLine.getLibraryItem() == itemLine.getLibraryItem()) {
                    return false;
                }
            }
        }

        LibraryItem libraryItem = itemLine.getLibraryItem();

        // Library item must be available
        if (libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }

        // Modify the library item's status from available to hold only if
        // it's a print format book item or disc digital item
        if (isPrintBookOrDiscDigital(libraryItem)) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        }
        // Otherwise, keep the status as Available

        this.itemLines.add(itemLine);
        return true;
    }

    public int removeItemLine(ItemLine itemLine) {
        // If the order is completed, return -1
        if (this.status != OrderStatus.PENDING) {
            return -1;
        }

        // Find and remove the item line
        if (this.itemLines != null) {
            for (int i = 0; i < this.itemLines.size(); i++) {
                ItemLine existingLine = this.itemLines.get(i);
                if (existingLine.getLibraryItem() == itemLine.getLibraryItem()) {
                    LibraryItem libraryItem = existingLine.getLibraryItem();

                    // Reset the library item's status to available only if it was previously Hold
                    if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                        libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
                    }
                    // Otherwise, retain its original status

                    this.itemLines.remove(i);
                    return this.itemLines.size();
                }
            }
        }

        return -1;
    }

    public void handleOrder() {
        // First, update Order Status to "completed"
        this.status = OrderStatus.COMPLETED;

        // Then modify the library item's status from hold to loan only if
        // the library item belongs to a print format book item or disc digital item
        if (this.itemLines != null) {
            for (ItemLine itemLine : this.itemLines) {
                LibraryItem libraryItem = itemLine.getLibraryItem();
                if (isPrintBookOrDiscDigital(libraryItem)) {
                    libraryItem.setStatus(LibraryItemStatus.LOAN);
                }
            }
        }
    }

    public int countPrintBookItemIfCompleted() {
        // Only count if the order status is completed
        if (this.status != OrderStatus.COMPLETED) {
            return 0;
        }

        int count = 0;
        if (this.itemLines != null) {
            for (ItemLine itemLine : this.itemLines) {
                LibraryItem libraryItem = itemLine.getLibraryItem();
                if (libraryItem instanceof BookItem) {
                    BookItem bookItem = (BookItem) libraryItem;
                    if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}

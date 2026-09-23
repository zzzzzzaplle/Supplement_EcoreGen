import java.util.ArrayList;
import java.util.List;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.itemLines = new ArrayList<>();
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
            return false;
        }
        LibraryItem libItem = itemLine.getLibraryItem();
        if (libItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        for (ItemLine existing : this.itemLines) {
            if (existing.getLibraryItem() == libItem) {
                return false;
            }
        }
        if (libItem instanceof BookItem) {
            BookItem bookItem = (BookItem) libItem;
            if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                libItem.setStatus(LibraryItemStatus.HOLD);
            }
        } else if (libItem instanceof DigitalItem) {
            DigitalItem digitalItem = (DigitalItem) libItem;
            if (digitalItem.getOption() == DigitalItemOption.DISC) {
                libItem.setStatus(LibraryItemStatus.HOLD);
            }
        }
        this.itemLines.add(itemLine);
        return true;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
            return -1;
        }
        boolean removed = this.itemLines.remove(itemLine);
        if (removed) {
            LibraryItem libItem = itemLine.getLibraryItem();
            if (libItem.getStatus() == LibraryItemStatus.HOLD) {
                libItem.setStatus(LibraryItemStatus.AVAILABLE);
            }
            return this.itemLines.size();
        }
        return -1;
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine itemLine : this.itemLines) {
            LibraryItem libItem = itemLine.getLibraryItem();
            if (libItem instanceof BookItem) {
                BookItem bookItem = (BookItem) libItem;
                if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                    libItem.setStatus(LibraryItemStatus.LOAN);
                }
            } else if (libItem instanceof DigitalItem) {
                DigitalItem digitalItem = (DigitalItem) libItem;
                if (digitalItem.getOption() == DigitalItemOption.DISC) {
                    libItem.setStatus(LibraryItemStatus.LOAN);
                }
            }
        }
    }

    public int countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
            return 0;
        }
        int count = 0;
        for (ItemLine itemLine : this.itemLines) {
            LibraryItem libItem = itemLine.getLibraryItem();
            if (libItem instanceof BookItem) {
                BookItem bookItem = (BookItem) libItem;
                if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                    count++;
                }
            }
        }
        return count;
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
}

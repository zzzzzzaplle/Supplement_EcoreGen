import java.util.ArrayList;
import java.util.List;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.status = OrderStatus.PENDING;
        this.itemLines = new ArrayList<ItemLine>();
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (itemLine == null) {
            return false;
        }
        if (this.status != OrderStatus.PENDING) {
            return false;
        }
        LibraryItem item = itemLine.getLibraryItem();
        if (item == null) {
            return false;
        }
        if (item.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        if (this.itemLines == null) {
            this.itemLines = new ArrayList<ItemLine>();
        }
        for (ItemLine existing : this.itemLines) {
            if (existing == null) {
                continue;
            }
            if (existing.getLibraryItem() == item) {
                return false;
            }
        }
        if (isPrintOrDiscItem(item)) {
            item.setStatus(LibraryItemStatus.HOLD);
        }
        this.itemLines.add(itemLine);
        return true;
    }

    public int countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
            return 0;
        }
        if (this.itemLines == null) {
            return 0;
        }
        int count = 0;
        for (ItemLine line : this.itemLines) {
            if (line == null) {
                continue;
            }
            LibraryItem item = line.getLibraryItem();
            if (item instanceof BookItem) {
                BookItem book = (BookItem) item;
                if (book.getType() == BookItemType.PRINT_FORMAT) {
                    count++;
                }
            }
        }
        return count;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
            return -1;
        }
        if (itemLine == null || this.itemLines == null) {
            return this.itemLines == null ? 0 : this.itemLines.size();
        }
        boolean removed = this.itemLines.remove(itemLine);
        if (removed) {
            LibraryItem item = itemLine.getLibraryItem();
            if (item != null
                    && isPrintOrDiscItem(item)
                    && item.getStatus() == LibraryItemStatus.HOLD) {
                item.setStatus(LibraryItemStatus.AVAILABLE);
            }
        }
        return this.itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        if (this.itemLines == null) {
            return;
        }
        for (ItemLine line : this.itemLines) {
            if (line == null) {
                continue;
            }
            LibraryItem item = line.getLibraryItem();
            if (item == null) {
                continue;
            }
            if (isPrintOrDiscItem(item)
                    && item.getStatus() == LibraryItemStatus.HOLD) {
                item.setStatus(LibraryItemStatus.LOAN);
            }
        }
    }

    private boolean isPrintOrDiscItem(LibraryItem item) {
        if (item instanceof BookItem) {
            BookItem book = (BookItem) item;
            return book.getType() == BookItemType.PRINT_FORMAT;
        }
        if (item instanceof DigitalItem) {
            DigitalItem digital = (DigitalItem) item;
            return digital.getOption() == DigitalItemOption.DISC;
        }
        return false;
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

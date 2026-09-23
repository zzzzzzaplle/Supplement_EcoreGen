import java.util.ArrayList;
import java.util.List;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.status = OrderStatus.PENDING;
        this.itemLines = new ArrayList<>();
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) return false;
        LibraryItem item = itemLine.getLibraryItem();
        if (item.getStatus() != LibraryItemStatus.AVAILABLE) return false;
        for (ItemLine il : itemLines) {
            if (il.getLibraryItem() == item) return false;
        }

        if (item instanceof BookItem) {
            if (((BookItem) item).getType() == BookItemType.PRINT_FORMAT) {
                item.setStatus(LibraryItemStatus.HOLD);
            }
        } else if (item instanceof DigitalItem) {
            if (((DigitalItem) item).getOption() == DigitalItemOption.DISC) {
                item.setStatus(LibraryItemStatus.HOLD);
            }
        }
        itemLines.add(itemLine);
        return true;
    }

    public int countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) return 0;
        int count = 0;
        for (ItemLine il : itemLines) {
            if (il.getLibraryItem() instanceof BookItem) {
                if (((BookItem) il.getLibraryItem()).getType() == BookItemType.PRINT_FORMAT) {
                    count++;
                }
            }
        }
        return count;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) return -1;
        if (itemLines.remove(itemLine)) {
            LibraryItem item = itemLine.getLibraryItem();
            if (item.getStatus() == LibraryItemStatus.HOLD) {
                item.setStatus(LibraryItemStatus.AVAILABLE);
            }
            return itemLines.size();
        }
        return itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine il : itemLines) {
            LibraryItem item = il.getLibraryItem();
            if (item.getStatus() == LibraryItemStatus.HOLD) {
                if (item instanceof BookItem) {
                    if (((BookItem) item).getType() == BookItemType.PRINT_FORMAT) {
                        item.setStatus(LibraryItemStatus.LOAN);
                    }
                } else if (item instanceof DigitalItem) {
                    if (((DigitalItem) item).getOption() == DigitalItemOption.DISC) {
                        item.setStatus(LibraryItemStatus.LOAN);
                    }
                }
            }
        }
    }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public List<ItemLine> getItemLines() { return itemLines; }
    public void setItemLines(List<ItemLine> itemLines) { this.itemLines = itemLines; }
}

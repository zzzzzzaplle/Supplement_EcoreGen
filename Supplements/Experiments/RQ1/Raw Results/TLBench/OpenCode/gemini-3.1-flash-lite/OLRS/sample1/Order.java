import java.util.ArrayList;
import java.util.List;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines = new ArrayList<>();
    public Order() {}
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public List<ItemLine> getItemLines() { return itemLines; }
    public void setItemLines(List<ItemLine> itemLines) { this.itemLines = itemLines; }

    public boolean addItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING) return false;
        LibraryItem item = itemLine.getLibraryItem();
        if (item.getStatus() != LibraryItemStatus.AVAILABLE) return false;
        for (ItemLine line : itemLines) {
            if (line.getLibraryItem() == item) return false;
        }
        itemLines.add(itemLine);
        if (item instanceof BookItem && ((BookItem) item).getType() == BookItemType.PRINT_FORMAT) {
            item.setStatus(LibraryItemStatus.HOLD);
        } else if (item instanceof DigitalItem && ((DigitalItem) item).getOption() == DigitalItemOption.DISC) {
            item.setStatus(LibraryItemStatus.HOLD);
        }
        return true;
    }

    public int countPrintBookItemIfCompleted() {
        if (status != OrderStatus.COMPLETED) return 0;
        int count = 0;
        for (ItemLine line : itemLines) {
            if (line.getLibraryItem() instanceof BookItem && ((BookItem) line.getLibraryItem()).getType() == BookItemType.PRINT_FORMAT) {
                count++;
            }
        }
        return count;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING) return -1;
        if (itemLines.remove(itemLine)) {
            LibraryItem item = itemLine.getLibraryItem();
            if (item.getStatus() == LibraryItemStatus.HOLD) {
                item.setStatus(LibraryItemStatus.AVAILABLE);
            }
            return itemLines.size();
        }
        return -1;
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine line : itemLines) {
            LibraryItem item = line.getLibraryItem();
            if (item.getStatus() == LibraryItemStatus.HOLD) {
                if (item instanceof BookItem && ((BookItem) item).getType() == BookItemType.PRINT_FORMAT) {
                    item.setStatus(LibraryItemStatus.LOAN);
                } else if (item instanceof DigitalItem && ((DigitalItem) item).getOption() == DigitalItemOption.DISC) {
                    item.setStatus(LibraryItemStatus.LOAN);
                }
            }
        }
    }
}

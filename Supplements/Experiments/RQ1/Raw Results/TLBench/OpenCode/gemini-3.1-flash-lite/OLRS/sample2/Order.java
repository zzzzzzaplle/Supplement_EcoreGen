import java.util.ArrayList;
import java.util.List;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.itemLines = new ArrayList<>();
    }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public List<ItemLine> getItemLines() { return itemLines; }
    public void setItemLines(List<ItemLine> itemLines) { this.itemLines = itemLines; }

    public boolean addItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING) return false;
        if (itemLine.getLibraryItem().getStatus() != LibraryItemStatus.AVAILABLE) return false;
        for (ItemLine il : itemLines) {
            if (il.getLibraryItem().equals(itemLine.getLibraryItem())) return false;
        }
        
        itemLines.add(itemLine);
        LibraryItem item = itemLine.getLibraryItem();
        if ((item instanceof BookItem && ((BookItem) item).getType() == BookItemType.PRINT_FORMAT) ||
            (item instanceof DigitalItem && ((DigitalItem) item).getOption() == DigitalItemOption.DISC)) {
            item.setStatus(LibraryItemStatus.HOLD);
        }
        return true;
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
        return itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine il : itemLines) {
            LibraryItem item = il.getLibraryItem();
            if (item.getStatus() == LibraryItemStatus.HOLD) {
                if ((item instanceof BookItem && ((BookItem) item).getType() == BookItemType.PRINT_FORMAT) ||
                    (item instanceof DigitalItem && ((DigitalItem) item).getOption() == DigitalItemOption.DISC)) {
                    item.setStatus(LibraryItemStatus.LOAN);
                }
            }
        }
    }

    public int countPrintBookItemIfCompleted() {
        if (status != OrderStatus.COMPLETED) return 0;
        int count = 0;
        for (ItemLine il : itemLines) {
            if (il.getLibraryItem() instanceof BookItem && ((BookItem) il.getLibraryItem()).getType() == BookItemType.PRINT_FORMAT) {
                count += il.getQuantity();
            }
        }
        return count;
    }
}

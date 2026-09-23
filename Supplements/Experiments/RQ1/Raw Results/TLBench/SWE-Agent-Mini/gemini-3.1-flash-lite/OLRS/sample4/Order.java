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

    public boolean addItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) return false;
        if (itemLine.getLibraryItem().getStatus() != LibraryItemStatus.AVAILABLE) return false;
        
        for (ItemLine il : itemLines) {
            if (il.getLibraryItem() == itemLine.getLibraryItem()) return false;
        }

        LibraryItem item = itemLine.getLibraryItem();
        boolean holdCondition = false;
        if (item instanceof BookItem) {
            if (((BookItem) item).getType() == BookItemType.PRINT_FORMAT) holdCondition = true;
        } else if (item instanceof DigitalItem) {
            if (((DigitalItem) item).getOption() == DigitalItemOption.DISC) holdCondition = true;
        }

        if (holdCondition) {
            item.setStatus(LibraryItemStatus.HOLD);
        }

        this.itemLines.add(itemLine);
        return true;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) return -1;
        
        if (this.itemLines.remove(itemLine)) {
            LibraryItem item = itemLine.getLibraryItem();
            if (item.getStatus() == LibraryItemStatus.HOLD) {
                item.setStatus(LibraryItemStatus.AVAILABLE);
            }
            return this.itemLines.size();
        }
        return -1;
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine il : this.itemLines) {
            LibraryItem item = il.getLibraryItem();
            if (item.getStatus() == LibraryItemStatus.HOLD) {
                boolean loanCondition = false;
                if (item instanceof BookItem) {
                    if (((BookItem) item).getType() == BookItemType.PRINT_FORMAT) loanCondition = true;
                } else if (item instanceof DigitalItem) {
                    if (((DigitalItem) item).getOption() == DigitalItemOption.DISC) loanCondition = true;
                }
                
                if (loanCondition) {
                    item.setStatus(LibraryItemStatus.LOAN);
                }
            }
        }
    }

    public int countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) return 0;
        int count = 0;
        for (ItemLine il : itemLines) {
            if (il.getLibraryItem() instanceof BookItem) {
                if (((BookItem) il.getLibraryItem()).getType() == BookItemType.PRINT_FORMAT) {
                    count += il.getQuantity();
                }
            }
        }
        return count;
    }
}

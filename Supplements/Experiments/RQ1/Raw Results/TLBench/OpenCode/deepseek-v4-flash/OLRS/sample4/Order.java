import java.util.ArrayList;
import java.util.List;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.itemLines = new ArrayList<ItemLine>();
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING) {
            return false;
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem.getStatus() == LibraryItemStatus.HOLD || libraryItem.getStatus() == LibraryItemStatus.LOAN) {
            return false;
        }
        for (ItemLine existing : itemLines) {
            if (existing.getLibraryItem() == libraryItem) {
                return false;
            }
        }
        itemLines.add(itemLine);
        if (libraryItem instanceof BookItem && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        } else if (libraryItem instanceof DigitalItem && ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        }
        return true;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING) {
            return -1;
        }
        itemLines.remove(itemLine);
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
            if (libraryItem instanceof BookItem && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT) {
                libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
            } else if (libraryItem instanceof DigitalItem && ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC) {
                libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
            }
        }
        return itemLines.size();
    }

    public void handleOrder() {
        status = OrderStatus.COMPLETED;
        for (ItemLine itemLine : itemLines) {
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                if (libraryItem instanceof BookItem && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT) {
                    libraryItem.setStatus(LibraryItemStatus.LOAN);
                } else if (libraryItem instanceof DigitalItem && ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC) {
                    libraryItem.setStatus(LibraryItemStatus.LOAN);
                }
            }
        }
    }

    public int countPrintBookItemIfCompleted() {
        if (status != OrderStatus.COMPLETED) {
            return 0;
        }
        int count = 0;
        for (ItemLine itemLine : itemLines) {
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem instanceof BookItem && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT) {
                count++;
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

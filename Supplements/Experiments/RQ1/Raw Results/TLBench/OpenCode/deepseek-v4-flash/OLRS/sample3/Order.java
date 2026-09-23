import java.util.List;
import java.util.ArrayList;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.itemLines = new ArrayList<ItemLine>();
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
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
        if (libraryItem instanceof BookItem && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        } else if (libraryItem instanceof DigitalItem && ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        }
        itemLines.add(itemLine);
        return true;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
            return -1;
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem instanceof BookItem && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT) {
            if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
            }
        } else if (libraryItem instanceof DigitalItem && ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC) {
            if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
            }
        }
        itemLines.remove(itemLine);
        return itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine itemLine : itemLines) {
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem instanceof BookItem && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT) {
                if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                    libraryItem.setStatus(LibraryItemStatus.LOAN);
                }
            } else if (libraryItem instanceof DigitalItem && ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC) {
                if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                    libraryItem.setStatus(LibraryItemStatus.LOAN);
                }
            }
        }
    }

    public int countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
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

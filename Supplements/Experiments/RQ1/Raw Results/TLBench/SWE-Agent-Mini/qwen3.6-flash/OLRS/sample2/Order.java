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
        if (status != OrderStatus.PENDING) {
            return false;
        }
        if (itemLine == null || itemLine.getLibraryItem() == null) {
            return false;
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        for (ItemLine existingLine : itemLines) {
            if (existingLine.getLibraryItem() == libraryItem) {
                return false;
            }
        }
        if (isPrintFormBook(libraryItem) || isDiscDigitalItem(libraryItem)) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        }
        itemLines.add(itemLine);
        return true;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING) {
            return -1;
        }
        if (itemLine == null || itemLines == null) {
            return -1;
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem != null && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
            libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
        }
        itemLines.remove(itemLine);
        return itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine itemLine : itemLines) {
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (isPrintFormBook(libraryItem) || isDiscDigitalItem(libraryItem)) {
                libraryItem.setStatus(LibraryItemStatus.LOAN);
            }
        }
    }

    public int countPrintBookItemIfCompleted() {
        if (status != OrderStatus.COMPLETED) {
            return 0;
        }
        int count = 0;
        if (itemLines != null) {
            for (ItemLine itemLine : itemLines) {
                LibraryItem libraryItem = itemLine.getLibraryItem();
                if (isPrintFormBook(libraryItem)) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isPrintFormBook(LibraryItem libraryItem) {
        if (libraryItem instanceof BookItem) {
            BookItem bookItem = (BookItem) libraryItem;
            return bookItem.getType() == BookItemType.PRINT_FORMAT;
        }
        return false;
    }

    private boolean isDiscDigitalItem(LibraryItem libraryItem) {
        if (libraryItem instanceof DigitalItem) {
            DigitalItem digitalItem = (DigitalItem) libraryItem;
            return digitalItem.getOption() == DigitalItemOption.DISC;
        }
        return false;
    }
}

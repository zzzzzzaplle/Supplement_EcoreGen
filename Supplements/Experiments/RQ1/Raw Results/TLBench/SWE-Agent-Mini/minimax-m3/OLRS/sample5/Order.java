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
        if (this.status != OrderStatus.PENDING) {
            return false;
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem == null) {
            return false;
        }
        if (libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        for (ItemLine existing : this.itemLines) {
            if (existing.getLibraryItem() == libraryItem) {
                return false;
            }
        }
        this.itemLines.add(itemLine);

        boolean isPrintBook = (libraryItem instanceof BookItem)
                && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT;
        boolean isDiscDigital = (libraryItem instanceof DigitalItem)
                && ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC;

        if (isPrintBook || isDiscDigital) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        } else {
            libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
        }
        return true;
    }

    public Integer countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
            return 0;
        }
        int count = 0;
        for (ItemLine line : this.itemLines) {
            LibraryItem item = line.getLibraryItem();
            if (item instanceof BookItem
                    && ((BookItem) item).getType() == BookItemType.PRINT_FORMAT) {
                count++;
            }
        }
        return count;
    }

    public Integer removeItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
            return -1;
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem != null && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
            boolean isPrintBook = (libraryItem instanceof BookItem)
                    && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT;
            boolean isDiscDigital = (libraryItem instanceof DigitalItem)
                    && ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC;
            if (isPrintBook || isDiscDigital) {
                libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
            }
        }
        this.itemLines.remove(itemLine);
        return this.itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine line : this.itemLines) {
            LibraryItem libraryItem = line.getLibraryItem();
            if (libraryItem == null) {
                continue;
            }
            if (libraryItem.getStatus() != LibraryItemStatus.HOLD) {
                continue;
            }
            boolean isPrintBook = (libraryItem instanceof BookItem)
                    && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT;
            boolean isDiscDigital = (libraryItem instanceof DigitalItem)
                    && ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC;
            if (isPrintBook || isDiscDigital) {
                libraryItem.setStatus(LibraryItemStatus.LOAN);
            }
        }
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

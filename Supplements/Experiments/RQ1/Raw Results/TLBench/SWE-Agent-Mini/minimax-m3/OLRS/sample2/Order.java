import java.util.ArrayList;
import java.util.List;

public class Order {

    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.itemLines = new ArrayList<ItemLine>();
        this.status = OrderStatus.PENDING;
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
            return false;
        }
        if (itemLine == null) {
            return false;
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem == null) {
            return false;
        }
        if (libraryItem.getStatus() == LibraryItemStatus.HOLD
                || libraryItem.getStatus() == LibraryItemStatus.LOAN) {
            return false;
        }
        for (ItemLine existing : this.itemLines) {
            if (existing != null && existing.getLibraryItem() == libraryItem) {
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

    public Integer removeItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
            return -1;
        }
        if (itemLine == null) {
            return this.itemLines.size();
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (this.itemLines.remove(itemLine)) {
            if (libraryItem != null && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                boolean isPrintBook = (libraryItem instanceof BookItem)
                        && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT;
                boolean isDiscDigital = (libraryItem instanceof DigitalItem)
                        && ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC;
                if (isPrintBook || isDiscDigital) {
                    libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
                }
            }
        }
        return this.itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine line : this.itemLines) {
            if (line == null) {
                continue;
            }
            LibraryItem item = line.getLibraryItem();
            if (item == null) {
                continue;
            }
            if (item.getStatus() != LibraryItemStatus.HOLD) {
                continue;
            }
            boolean isPrintBook = (item instanceof BookItem)
                    && ((BookItem) item).getType() == BookItemType.PRINT_FORMAT;
            boolean isDiscDigital = (item instanceof DigitalItem)
                    && ((DigitalItem) item).getOption() == DigitalItemOption.DISC;
            if (isPrintBook || isDiscDigital) {
                item.setStatus(LibraryItemStatus.LOAN);
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

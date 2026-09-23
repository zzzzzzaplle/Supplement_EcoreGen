import java.util.ArrayList;
import java.util.List;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.itemLines = new ArrayList<ItemLine>();
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (itemLine == null) {
            return false;
        }
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
        if (this.itemLines == null) {
            this.itemLines = new ArrayList<ItemLine>();
        }
        for (ItemLine existing : this.itemLines) {
            if (existing == null) {
                continue;
            }
            if (existing.getLibraryItem() == libraryItem) {
                return false;
            }
        }
        this.itemLines.add(itemLine);
        if (isPrintBook(libraryItem) || isDiscDigital(libraryItem)) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        }
        return true;
    }

    public Integer removeItemLine(ItemLine itemLine) {
        if (this.status == OrderStatus.COMPLETED) {
            return -1;
        }
        if (itemLine == null) {
            if (this.itemLines == null) {
                return 0;
            }
            return this.itemLines.size();
        }
        if (this.itemLines == null) {
            return 0;
        }
        boolean removed = this.itemLines.remove(itemLine);
        if (removed) {
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem != null) {
                if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                    if (isPrintBook(libraryItem) || isDiscDigital(libraryItem)) {
                        libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
                    }
                }
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
            LibraryItem libraryItem = line.getLibraryItem();
            if (libraryItem == null) {
                continue;
            }
            if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                if (isPrintBook(libraryItem) || isDiscDigital(libraryItem)) {
                    libraryItem.setStatus(LibraryItemStatus.LOAN);
                }
            }
        }
    }

    public Integer countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
            return 0;
        }
        if (this.itemLines == null || this.itemLines.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (ItemLine line : this.itemLines) {
            if (line == null) {
                continue;
            }
            LibraryItem libraryItem = line.getLibraryItem();
            if (libraryItem == null) {
                continue;
            }
            if (isPrintBook(libraryItem)) {
                Integer quantity = line.getQuantity();
                if (quantity != null) {
                    total += quantity;
                }
            }
        }
        return total;
    }

    private boolean isPrintBook(LibraryItem libraryItem) {
        if (libraryItem instanceof BookItem) {
            BookItem book = (BookItem) libraryItem;
            return book.getType() == BookItemType.PRINT_FORMAT;
        }
        return false;
    }

    private boolean isDiscDigital(LibraryItem libraryItem) {
        if (libraryItem instanceof DigitalItem) {
            DigitalItem digital = (DigitalItem) libraryItem;
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

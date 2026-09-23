import java.util.List;
import java.util.ArrayList;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.itemLines = new ArrayList<>();
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING) {
            return false;
        }

        LibraryItem libItem = itemLine.getLibraryItem();
        if (libItem.getStatus() == LibraryItemStatus.HOLD || libItem.getStatus() == LibraryItemStatus.LOAN) {
            return false;
        }

        for (ItemLine existing : itemLines) {
            if (existing.getLibraryItem().equals(libItem)) {
                return false;
            }
        }

        itemLines.add(itemLine);

        if (libItem instanceof BookItem) {
            BookItem bookItem = (BookItem) libItem;
            if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                libItem.setStatus(LibraryItemStatus.HOLD);
            }
        } else if (libItem instanceof DigitalItem) {
            DigitalItem digitalItem = (DigitalItem) libItem;
            if (digitalItem.getOption() == DigitalItemOption.DISC) {
                libItem.setStatus(LibraryItemStatus.HOLD);
            }
        }

        return true;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING) {
            return -1;
        }

        boolean removed = itemLines.remove(itemLine);
        if (removed) {
            LibraryItem libItem = itemLine.getLibraryItem();
            if (libItem.getStatus() == LibraryItemStatus.HOLD) {
                if (libItem instanceof BookItem) {
                    BookItem bookItem = (BookItem) libItem;
                    if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                        libItem.setStatus(LibraryItemStatus.AVAILABLE);
                    }
                } else if (libItem instanceof DigitalItem) {
                    DigitalItem digitalItem = (DigitalItem) libItem;
                    if (digitalItem.getOption() == DigitalItemOption.DISC) {
                        libItem.setStatus(LibraryItemStatus.AVAILABLE);
                    }
                }
            }
            return itemLines.size();
        }
        return itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine itemLine : itemLines) {
            LibraryItem libItem = itemLine.getLibraryItem();
            if (libItem.getStatus() == LibraryItemStatus.HOLD) {
                if (libItem instanceof BookItem) {
                    BookItem bookItem = (BookItem) libItem;
                    if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                        libItem.setStatus(LibraryItemStatus.LOAN);
                    }
                } else if (libItem instanceof DigitalItem) {
                    DigitalItem digitalItem = (DigitalItem) libItem;
                    if (digitalItem.getOption() == DigitalItemOption.DISC) {
                        libItem.setStatus(LibraryItemStatus.LOAN);
                    }
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
            LibraryItem libItem = itemLine.getLibraryItem();
            if (libItem instanceof BookItem) {
                BookItem bookItem = (BookItem) libItem;
                if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                    count++;
                }
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

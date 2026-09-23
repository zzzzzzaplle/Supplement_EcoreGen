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
        if (itemLine == null) {
            return false;
        }
        if (this.status != OrderStatus.PENDING) {
            return false;
        }
        LibraryItem libItem = itemLine.getLibraryItem();
        if (libItem == null) {
            return false;
        }
        if (libItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        for (ItemLine existing : this.itemLines) {
            if (existing != null && existing.getLibraryItem() == libItem) {
                return false;
            }
        }
        this.itemLines.add(itemLine);
        boolean shouldHold = false;
        if (libItem instanceof BookItem) {
            BookItem book = (BookItem) libItem;
            if (book.getType() == BookItemType.PRINT_FORMAT) {
                shouldHold = true;
            }
        } else if (libItem instanceof DigitalItem) {
            DigitalItem dig = (DigitalItem) libItem;
            if (dig.getOption() == DigitalItemOption.DISC) {
                shouldHold = true;
            }
        }
        if (shouldHold) {
            libItem.setStatus(LibraryItemStatus.HOLD);
        } else {
            libItem.setStatus(LibraryItemStatus.AVAILABLE);
        }
        return true;
    }

    public int countPrintBookItemIfCompleted() {
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

    public int removeItemLine(ItemLine itemLine) {
        if (this.status == OrderStatus.COMPLETED) {
            return -1;
        }
        if (itemLine == null) {
            return this.itemLines.size();
        }
        if (this.itemLines.remove(itemLine)) {
            LibraryItem libItem = itemLine.getLibraryItem();
            if (libItem != null && libItem.getStatus() == LibraryItemStatus.HOLD) {
                boolean wasHoldFromPrintOrDisc = false;
                if (libItem instanceof BookItem) {
                    BookItem book = (BookItem) libItem;
                    if (book.getType() == BookItemType.PRINT_FORMAT) {
                        wasHoldFromPrintOrDisc = true;
                    }
                } else if (libItem instanceof DigitalItem) {
                    DigitalItem dig = (DigitalItem) libItem;
                    if (dig.getOption() == DigitalItemOption.DISC) {
                        wasHoldFromPrintOrDisc = true;
                    }
                }
                if (wasHoldFromPrintOrDisc) {
                    libItem.setStatus(LibraryItemStatus.AVAILABLE);
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
            LibraryItem libItem = line.getLibraryItem();
            if (libItem == null) {
                continue;
            }
            if (libItem.getStatus() == LibraryItemStatus.HOLD) {
                boolean eligible = false;
                if (libItem instanceof BookItem) {
                    BookItem book = (BookItem) libItem;
                    if (book.getType() == BookItemType.PRINT_FORMAT) {
                        eligible = true;
                    }
                } else if (libItem instanceof DigitalItem) {
                    DigitalItem dig = (DigitalItem) libItem;
                    if (dig.getOption() == DigitalItemOption.DISC) {
                        eligible = true;
                    }
                }
                if (eligible) {
                    libItem.setStatus(LibraryItemStatus.LOAN);
                }
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

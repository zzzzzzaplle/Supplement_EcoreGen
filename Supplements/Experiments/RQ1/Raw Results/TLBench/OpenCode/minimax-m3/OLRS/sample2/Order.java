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
        LibraryItem newItem = itemLine.getLibraryItem();
        if (newItem == null) {
            return false;
        }
        if (newItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        for (ItemLine existing : this.itemLines) {
            if (existing != null && existing.getLibraryItem() == newItem) {
                return false;
            }
        }
        boolean needsHold = false;
        if (newItem instanceof BookItem) {
            BookItem book = (BookItem) newItem;
            if (book.getType() == BookItemType.PRINT_FORMAT) {
                needsHold = true;
            }
        } else if (newItem instanceof DigitalItem) {
            DigitalItem digital = (DigitalItem) newItem;
            if (digital.getOption() == DigitalItemOption.DISC) {
                needsHold = true;
            }
        }
        if (needsHold) {
            newItem.setStatus(LibraryItemStatus.HOLD);
        }
        this.itemLines.add(itemLine);
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
        boolean removed = this.itemLines.remove(itemLine);
        if (removed) {
            LibraryItem removedItem = itemLine.getLibraryItem();
            if (removedItem != null && removedItem.getStatus() == LibraryItemStatus.HOLD) {
                boolean needsReset = false;
                if (removedItem instanceof BookItem) {
                    BookItem book = (BookItem) removedItem;
                    if (book.getType() == BookItemType.PRINT_FORMAT) {
                        needsReset = true;
                    }
                } else if (removedItem instanceof DigitalItem) {
                    DigitalItem digital = (DigitalItem) removedItem;
                    if (digital.getOption() == DigitalItemOption.DISC) {
                        needsReset = true;
                    }
                }
                if (needsReset) {
                    removedItem.setStatus(LibraryItemStatus.AVAILABLE);
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
            boolean shouldLoan = false;
            if (item instanceof BookItem) {
                BookItem book = (BookItem) item;
                if (book.getType() == BookItemType.PRINT_FORMAT) {
                    shouldLoan = true;
                }
            } else if (item instanceof DigitalItem) {
                DigitalItem digital = (DigitalItem) item;
                if (digital.getOption() == DigitalItemOption.DISC) {
                    shouldLoan = true;
                }
            }
            if (shouldLoan) {
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

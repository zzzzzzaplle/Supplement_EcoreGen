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
        LibraryItem li = itemLine.getLibraryItem();
        if (li == null) {
            return false;
        }
        if (li.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        if (this.itemLines == null) {
            this.itemLines = new ArrayList<ItemLine>();
        }
        for (ItemLine existing : this.itemLines) {
            if (existing == null) {
                continue;
            }
            if (existing.getLibraryItem() == li) {
                return false;
            }
        }
        boolean shouldHold = false;
        if (li instanceof BookItem) {
            BookItem bi = (BookItem) li;
            if (bi.getType() == BookItemType.PRINT_FORMAT) {
                shouldHold = true;
            }
        } else if (li instanceof DigitalItem) {
            DigitalItem di = (DigitalItem) li;
            if (di.getOption() == DigitalItemOption.DISC) {
                shouldHold = true;
            }
        }
        if (shouldHold) {
            li.setStatus(LibraryItemStatus.HOLD);
        } else {
            li.setStatus(LibraryItemStatus.AVAILABLE);
        }
        this.itemLines.add(itemLine);
        return true;
    }

    public int countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
            return 0;
        }
        if (this.itemLines == null) {
            return 0;
        }
        int count = 0;
        for (ItemLine line : this.itemLines) {
            if (line == null) {
                continue;
            }
            LibraryItem li = line.getLibraryItem();
            if (li instanceof BookItem) {
                BookItem bi = (BookItem) li;
                if (bi.getType() == BookItemType.PRINT_FORMAT) {
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
            return this.itemLines == null ? 0 : this.itemLines.size();
        }
        if (this.itemLines == null) {
            return 0;
        }
        boolean removed = this.itemLines.remove(itemLine);
        if (removed) {
            LibraryItem li = itemLine.getLibraryItem();
            if (li != null && li.getStatus() == LibraryItemStatus.HOLD) {
                boolean shouldReset = false;
                if (li instanceof BookItem) {
                    BookItem bi = (BookItem) li;
                    if (bi.getType() == BookItemType.PRINT_FORMAT) {
                        shouldReset = true;
                    }
                } else if (li instanceof DigitalItem) {
                    DigitalItem di = (DigitalItem) li;
                    if (di.getOption() == DigitalItemOption.DISC) {
                        shouldReset = true;
                    }
                }
                if (shouldReset) {
                    li.setStatus(LibraryItemStatus.AVAILABLE);
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
            LibraryItem li = line.getLibraryItem();
            if (li == null) {
                continue;
            }
            if (li.getStatus() == LibraryItemStatus.HOLD) {
                boolean shouldLoan = false;
                if (li instanceof BookItem) {
                    BookItem bi = (BookItem) li;
                    if (bi.getType() == BookItemType.PRINT_FORMAT) {
                        shouldLoan = true;
                    }
                } else if (li instanceof DigitalItem) {
                    DigitalItem di = (DigitalItem) li;
                    if (di.getOption() == DigitalItemOption.DISC) {
                        shouldLoan = true;
                    }
                }
                if (shouldLoan) {
                    li.setStatus(LibraryItemStatus.LOAN);
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

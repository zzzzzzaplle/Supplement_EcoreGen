import java.util.ArrayList;
import java.util.List;

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
        if (itemLine == null) {
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
            if (existing != null && existing.getLibraryItem() == li) {
                return false;
            }
        }
        boolean needsHold = false;
        if (li instanceof BookItem) {
            BookItem bi = (BookItem) li;
            if (bi.getType() == BookItemType.PRINT_FORMAT) {
                needsHold = true;
            }
        } else if (li instanceof DigitalItem) {
            DigitalItem di = (DigitalItem) li;
            if (di.getOption() == DigitalItemOption.DISC) {
                needsHold = true;
            }
        }
        if (needsHold) {
            li.setStatus(LibraryItemStatus.HOLD);
        } else {
            li.setStatus(LibraryItemStatus.AVAILABLE);
        }
        this.itemLines.add(itemLine);
        return true;
    }

    public int countPrintBookItemIfCompleted() {
        int count = 0;
        if (this.status != OrderStatus.COMPLETED) {
            return 0;
        }
        if (this.itemLines == null) {
            return 0;
        }
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
            if (this.itemLines == null) {
                return 0;
            }
            return this.itemLines.size();
        }
        if (this.itemLines == null) {
            return 0;
        }
        boolean removed = this.itemLines.remove(itemLine);
        LibraryItem li = itemLine.getLibraryItem();
        if (li != null && li.getStatus() == LibraryItemStatus.HOLD) {
            boolean isPrintOrDisc = false;
            if (li instanceof BookItem) {
                BookItem bi = (BookItem) li;
                if (bi.getType() == BookItemType.PRINT_FORMAT) {
                    isPrintOrDisc = true;
                }
            } else if (li instanceof DigitalItem) {
                DigitalItem di = (DigitalItem) li;
                if (di.getOption() == DigitalItemOption.DISC) {
                    isPrintOrDisc = true;
                }
            }
            if (isPrintOrDisc) {
                li.setStatus(LibraryItemStatus.AVAILABLE);
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
            if (li.getStatus() != LibraryItemStatus.HOLD) {
                continue;
            }
            boolean isPrintOrDisc = false;
            if (li instanceof BookItem) {
                BookItem bi = (BookItem) li;
                if (bi.getType() == BookItemType.PRINT_FORMAT) {
                    isPrintOrDisc = true;
                }
            } else if (li instanceof DigitalItem) {
                DigitalItem di = (DigitalItem) li;
                if (di.getOption() == DigitalItemOption.DISC) {
                    isPrintOrDisc = true;
                }
            }
            if (isPrintOrDisc) {
                li.setStatus(LibraryItemStatus.LOAN);
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

import java.util.ArrayList;
import java.util.List;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines = new ArrayList<>();

    public Order() {}

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
        if (this.getStatus() != OrderStatus.PENDING) {
            return false;
        }

        LibraryItem item = itemLine.getLibraryItem();
        if (item.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }

        for (ItemLine existingLine : itemLines) {
            if (existingLine.getLibraryItem() == item) {
                return false;
            }
        }

        itemLines.add(itemLine);

        boolean needsHold = false;
        if (item instanceof BookItem) {
            if (((BookItem) item).getType() == BookItemType.PRINT_FORMAT) {
                needsHold = true;
            }
        } else if (item instanceof DigitalItem) {
            if (((DigitalItem) item).getOption() == DigitalItemOption.DISC) {
                needsHold = true;
            }
        }

        if (needsHold) {
            item.setStatus(LibraryItemStatus.HOLD);
        }

        return true;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (this.getStatus() != OrderStatus.PENDING) {
            return -1;
        }

        if (itemLines.remove(itemLine)) {
            LibraryItem item = itemLine.getLibraryItem();
            if (item.getStatus() == LibraryItemStatus.HOLD) {
                item.setStatus(LibraryItemStatus.AVAILABLE);
            }
            return itemLines.size();
        }

        return -1;
    }

    public void handleOrder() {
        this.setStatus(OrderStatus.COMPLETED);
        for (ItemLine line : itemLines) {
            LibraryItem item = line.getLibraryItem();
            if (item.getStatus() == LibraryItemStatus.HOLD) {
                boolean isEligible = false;
                if (item instanceof BookItem && ((BookItem) item).getType() == BookItemType.PRINT_FORMAT) {
                    isEligible = true;
                } else if (item instanceof DigitalItem && ((DigitalItem) item).getOption() == DigitalItemOption.DISC) {
                    isEligible = true;
                }

                if (isEligible) {
                    item.setStatus(LibraryItemStatus.LOAN);
                }
            }
        }
    }

    public int countPrintBookItemIfCompleted() {
        if (this.getStatus() != OrderStatus.COMPLETED) {
            return 0;
        }
        int count = 0;
        for (ItemLine line : itemLines) {
            LibraryItem item = line.getLibraryItem();
            if (item instanceof BookItem && ((BookItem) item).getType() == BookItemType.PRINT_FORMAT) {
                count += line.getQuantity();
            }
        }
        return count;
    }
}

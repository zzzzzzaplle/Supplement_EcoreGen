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
        if (this.status != OrderStatus.PENDING) {
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

        boolean shouldHold = false;
        if (item instanceof BookItem) {
            if (((BookItem) item).getType() == BookItemType.PRINT_FORMAT) {
                shouldHold = true;
            }
        } else if (item instanceof DigitalItem) {
            if (((DigitalItem) item).getOption() == DigitalItemOption.DISC) {
                shouldHold = true;
            }
        }

        if (shouldHold) {
            item.setStatus(LibraryItemStatus.HOLD);
        }

        itemLines.add(itemLine);
        return true;
    }

    public Integer removeItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
            return -1;
        }

        if (!itemLines.remove(itemLine)) {
            return itemLines.size(); // Or handle not found
        }

        LibraryItem item = itemLine.getLibraryItem();
        if (item.getStatus() == LibraryItemStatus.HOLD) {
            item.setStatus(LibraryItemStatus.AVAILABLE);
        }

        return itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine line : itemLines) {
            LibraryItem item = line.getLibraryItem();
            if (item.getStatus() == LibraryItemStatus.HOLD) {
                boolean shouldLoan = false;
                if (item instanceof BookItem) {
                    if (((BookItem) item).getType() == BookItemType.PRINT_FORMAT) {
                        shouldLoan = true;
                    }
                } else if (item instanceof DigitalItem) {
                    if (((DigitalItem) item).getOption() == DigitalItemOption.DISC) {
                        shouldLoan = true;
                    }
                }
                if (shouldLoan) {
                    item.setStatus(LibraryItemStatus.LOAN);
                }
            }
        }
    }

    public Integer countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
            return 0;
        }
        int count = 0;
        for (ItemLine line : itemLines) {
            if (line.getLibraryItem() instanceof BookItem) {
                if (((BookItem) line.getLibraryItem()).getType() == BookItemType.PRINT_FORMAT) {
                    count += line.getQuantity();
                }
            }
        }
        return count;
    }
}

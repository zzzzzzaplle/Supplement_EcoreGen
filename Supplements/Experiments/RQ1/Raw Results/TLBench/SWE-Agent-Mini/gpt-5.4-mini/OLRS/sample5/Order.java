import java.util.ArrayList;
import java.util.List;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.itemLines = new ArrayList<ItemLine>();
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING || itemLine == null || itemLine.getLibraryItem() == null) {
            return false;
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        for (ItemLine existing : itemLines) {
            if (existing != null && existing.getLibraryItem() == libraryItem) {
                return false;
            }
        }
        itemLines.add(itemLine);
        boolean shouldHold = false;
        if (libraryItem instanceof BookItem) {
            BookItem bookItem = (BookItem) libraryItem;
            shouldHold = bookItem.getType() == BookItemType.PRINT_FORMAT;
        } else if (libraryItem instanceof DigitalItem) {
            DigitalItem digitalItem = (DigitalItem) libraryItem;
            shouldHold = digitalItem.getOption() == DigitalItemOption.DISC;
        }
        if (shouldHold) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        }
        return true;
    }

    public int countPrintBookItemIfCompleted() {
        if (status != OrderStatus.COMPLETED || itemLines == null) {
            return 0;
        }
        int count = 0;
        for (ItemLine itemLine : itemLines) {
            if (itemLine != null && itemLine.getLibraryItem() instanceof BookItem) {
                BookItem bookItem = (BookItem) itemLine.getLibraryItem();
                if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                    count += itemLine.getQuantity() == null ? 0 : itemLine.getQuantity();
                }
            }
        }
        return count;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING || itemLines == null || itemLine == null) {
            return -1;
        }
        int index = -1;
        for (int i = 0; i < itemLines.size(); i++) {
            ItemLine existing = itemLines.get(i);
            if (existing != null && existing.getLibraryItem() == itemLine.getLibraryItem()) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            return itemLines.size();
        }
        ItemLine removed = itemLines.remove(index);
        LibraryItem libraryItem = removed.getLibraryItem();
        if (libraryItem != null && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
            boolean shouldReset = false;
            if (libraryItem instanceof BookItem) {
                shouldReset = ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT;
            } else if (libraryItem instanceof DigitalItem) {
                shouldReset = ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC;
            }
            if (shouldReset) {
                libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
            }
        }
        return itemLines.size();
    }

    public void handleOrder() {
        status = OrderStatus.COMPLETED;
        if (itemLines == null) {
            return;
        }
        for (ItemLine itemLine : itemLines) {
            if (itemLine == null || itemLine.getLibraryItem() == null) {
                continue;
            }
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                boolean shouldLoan = false;
                if (libraryItem instanceof BookItem) {
                    shouldLoan = ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT;
                } else if (libraryItem instanceof DigitalItem) {
                    shouldLoan = ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC;
                }
                if (shouldLoan) {
                    libraryItem.setStatus(LibraryItemStatus.LOAN);
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

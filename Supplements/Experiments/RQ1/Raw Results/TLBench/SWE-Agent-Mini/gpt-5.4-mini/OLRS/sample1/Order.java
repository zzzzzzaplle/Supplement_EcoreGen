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
        for (ItemLine existing : itemLines) {
            if (existing != null && existing.getLibraryItem() == itemLine.getLibraryItem()) {
                return false;
            }
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        if (libraryItem instanceof BookItem) {
            BookItem bookItem = (BookItem) libraryItem;
            if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                libraryItem.setStatus(LibraryItemStatus.HOLD);
            }
        } else if (libraryItem instanceof DigitalItem) {
            DigitalItem digitalItem = (DigitalItem) libraryItem;
            if (digitalItem.getOption() == DigitalItemOption.DISC) {
                libraryItem.setStatus(LibraryItemStatus.HOLD);
            }
        }
        itemLines.add(itemLine);
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
        for (int i = 0; i < itemLines.size(); i++) {
            ItemLine existing = itemLines.get(i);
            if (existing != null && existing.getLibraryItem() == itemLine.getLibraryItem()) {
                LibraryItem libraryItem = existing.getLibraryItem();
                if (libraryItem != null && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                    libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
                }
                itemLines.remove(i);
                return itemLines.size();
            }
        }
        return itemLines.size();
    }

    public void handleOrder() {
        setStatus(OrderStatus.COMPLETED);
        if (itemLines == null) {
            return;
        }
        for (ItemLine itemLine : itemLines) {
            if (itemLine == null || itemLine.getLibraryItem() == null) {
                continue;
            }
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                if (libraryItem instanceof BookItem) {
                    BookItem bookItem = (BookItem) libraryItem;
                    if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                        libraryItem.setStatus(LibraryItemStatus.LOAN);
                    }
                } else if (libraryItem instanceof DigitalItem) {
                    DigitalItem digitalItem = (DigitalItem) libraryItem;
                    if (digitalItem.getOption() == DigitalItemOption.DISC) {
                        libraryItem.setStatus(LibraryItemStatus.LOAN);
                    }
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

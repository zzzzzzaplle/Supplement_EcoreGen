import java.util.List;
import java.util.ArrayList;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.itemLines = new ArrayList<>();
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
            return false;
        }
        if (itemLine == null || itemLine.getLibraryItem() == null) {
            return false;
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        for (ItemLine existing : this.itemLines) {
            if (existing.getLibraryItem() == libraryItem) {
                return false;
            }
        }
        if (libraryItem instanceof BookItem) {
            BookItemType type = ((BookItem) libraryItem).getType();
            if (type == BookItemType.PRINT_FORMAT) {
                libraryItem.setStatus(LibraryItemStatus.HOLD);
            }
        } else if (libraryItem instanceof DigitalItem) {
            DigitalItemOption option = ((DigitalItem) libraryItem).getOption();
            if (option == DigitalItemOption.DISC) {
                libraryItem.setStatus(LibraryItemStatus.HOLD);
            }
        }
        this.itemLines.add(itemLine);
        return true;
    }

    public int countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
            return 0;
        }
        int count = 0;
        for (ItemLine itemLine : this.itemLines) {
            LibraryItem item = itemLine.getLibraryItem();
            if (item instanceof BookItem) {
                BookItemType type = ((BookItem) item).getType();
                if (type == BookItemType.PRINT_FORMAT) {
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
        boolean removed = this.itemLines.remove(itemLine);
        if (!removed) {
            return -1;
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem != null && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
            if (libraryItem instanceof BookItem) {
                BookItemType type = ((BookItem) libraryItem).getType();
                if (type == BookItemType.PRINT_FORMAT) {
                    libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
                }
            } else if (libraryItem instanceof DigitalItem) {
                DigitalItemOption option = ((DigitalItem) libraryItem).getOption();
                if (option == DigitalItemOption.DISC) {
                    libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
                }
            }
        }
        return this.itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine itemLine : this.itemLines) {
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem instanceof BookItem) {
                BookItemType type = ((BookItem) libraryItem).getType();
                if (type == BookItemType.PRINT_FORMAT) {
                    libraryItem.setStatus(LibraryItemStatus.LOAN);
                }
            } else if (libraryItem instanceof DigitalItem) {
                DigitalItemOption option = ((DigitalItem) libraryItem).getOption();
                if (option == DigitalItemOption.DISC) {
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

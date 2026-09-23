import java.util.List;
import java.util.ArrayList;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.itemLines = new ArrayList<ItemLine>();
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
        if (status != OrderStatus.PENDING) {
            return false;
        }

        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem.getStatus() == LibraryItemStatus.HOLD || libraryItem.getStatus() == LibraryItemStatus.LOAN) {
            return false;
        }

        for (ItemLine existingLine : itemLines) {
            if (existingLine.getLibraryItem() == libraryItem) {
                return false;
            }
        }

        boolean changeToHold = false;
        if (libraryItem instanceof BookItem) {
            BookItem bookItem = (BookItem) libraryItem;
            if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                changeToHold = true;
            }
        } else if (libraryItem instanceof DigitalItem) {
            DigitalItem digitalItem = (DigitalItem) libraryItem;
            if (digitalItem.getOption() == DigitalItemOption.DISC) {
                changeToHold = true;
            }
        }

        if (changeToHold) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        }

        itemLines.add(itemLine);
        return true;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING) {
            return -1;
        }

        LibraryItem libraryItem = itemLine.getLibraryItem();

        boolean wasHold = false;
        if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
            if (libraryItem instanceof BookItem) {
                BookItem bookItem = (BookItem) libraryItem;
                if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                    wasHold = true;
                }
            } else if (libraryItem instanceof DigitalItem) {
                DigitalItem digitalItem = (DigitalItem) libraryItem;
                if (digitalItem.getOption() == DigitalItemOption.DISC) {
                    wasHold = true;
                }
            }
        }

        itemLines.remove(itemLine);

        if (wasHold) {
            libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
        }

        return itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;

        for (ItemLine itemLine : itemLines) {
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

    public int countPrintBookItemIfCompleted() {
        if (status != OrderStatus.COMPLETED) {
            return 0;
        }

        int count = 0;
        for (ItemLine itemLine : itemLines) {
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem instanceof BookItem) {
                BookItem bookItem = (BookItem) libraryItem;
                if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                    count++;
                }
            }
        }
        return count;
    }
}

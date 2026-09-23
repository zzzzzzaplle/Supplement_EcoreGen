import java.util.ArrayList;
import java.util.List;

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

        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem == null) {
            return false;
        }

        if (libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }

        for (ItemLine existingLine : this.itemLines) {
            if (existingLine.getLibraryItem() == libraryItem) {
                return false;
            }
        }

        BookItem bookItem = null;
        if (libraryItem instanceof BookItem) {
            bookItem = (BookItem) libraryItem;
        }

        if (bookItem != null && bookItem.getType() == BookItemType.PRINT_FORMAT) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        } else if (libraryItem instanceof DigitalItem) {
            DigitalItem digitalItem = (DigitalItem) libraryItem;
            if (digitalItem.getOption() == DigitalItemOption.DISC) {
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

    public int removeItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
            return -1;
        }

        if (!this.itemLines.contains(itemLine)) {
            return -1;
        }

        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
            BookItem bookItem = null;
            if (libraryItem instanceof BookItem) {
                bookItem = (BookItem) libraryItem;
            }

            boolean isPrintOrDisc = false;
            if (bookItem != null && bookItem.getType() == BookItemType.PRINT_FORMAT) {
                isPrintOrDisc = true;
            } else if (libraryItem instanceof DigitalItem) {
                DigitalItem digitalItem = (DigitalItem) libraryItem;
                if (digitalItem.getOption() == DigitalItemOption.DISC) {
                    isPrintOrDisc = true;
                }
            }

            if (isPrintOrDisc) {
                libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
            }
        }

        this.itemLines.remove(itemLine);
        return this.itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;

        for (ItemLine itemLine : this.itemLines) {
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem.getStatus() == LibraryItemStatus.AVAILABLE) {
                continue;
            }

            BookItem bookItem = null;
            if (libraryItem instanceof BookItem) {
                bookItem = (BookItem) libraryItem;
            }

            boolean isPrintOrDisc = false;
            if (bookItem != null && bookItem.getType() == BookItemType.PRINT_FORMAT) {
                isPrintOrDisc = true;
            } else if (libraryItem instanceof DigitalItem) {
                DigitalItem digitalItem = (DigitalItem) libraryItem;
                if (digitalItem.getOption() == DigitalItemOption.DISC) {
                    isPrintOrDisc = true;
                }
            }

            if (isPrintOrDisc && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                libraryItem.setStatus(LibraryItemStatus.LOAN);
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

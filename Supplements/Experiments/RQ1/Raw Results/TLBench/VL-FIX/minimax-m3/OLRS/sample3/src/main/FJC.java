import java.util.ArrayList;
import java.util.List;

enum OrderStatus {
    PENDING,
    COMPLETED
}

enum LibraryItemStatus {
    AVAILABLE,
    HOLD,
    LOAN
}

enum BookItemType {
    EBOOK,
    PRINT_FORMAT
}

enum DigitalItemOption {
    DOWNLOADABLE,
    DISC
}

enum DigitalItemType {
    AUDIO,
    VIDEO
}

abstract class LibraryItem {
    private LibraryItemStatus status;

    public LibraryItem() {
    }

    public LibraryItemStatus getStatus() {
        return status;
    }

    public void setStatus(LibraryItemStatus status) {
        this.status = status;
    }
}

class BookItem extends LibraryItem {
    private BookItemType type;

    public BookItem() {
    }

    public BookItemType getType() {
        return type;
    }

    public void setType(BookItemType type) {
        this.type = type;
    }
}

class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
    }

    public DigitalItemOption getOption() {
        return option;
    }

    public void setOption(DigitalItemOption option) {
        this.option = option;
    }

    public DigitalItemType getType() {
        return type;
    }

    public void setType(DigitalItemType type) {
        this.type = type;
    }

    public int calculateTotalDownloads(List<Order> orders) {
        int total = 0;
        if (orders == null) {
            return 0;
        }
        if (this.option != DigitalItemOption.DOWNLOADABLE || this.type != DigitalItemType.AUDIO) {
            return 0;
        }
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                for (ItemLine itemLine : order.getItemLines()) {
                    if (itemLine.getLibraryItem() == this) {
                        if (itemLine.getQuantity() != null) {
                            total += itemLine.getQuantity();
                        }
                    }
                }
            }
        }
        return total;
    }
}

class ItemLine {
    private Integer quantity;
    private LibraryItem libraryItem;

    public ItemLine() {
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LibraryItem getLibraryItem() {
        return libraryItem;
    }

    public void setLibraryItem(LibraryItem libraryItem) {
        this.libraryItem = libraryItem;
    }
}

class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
        this.status = OrderStatus.PENDING;
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
        for (ItemLine existing : itemLines) {
            if (existing.getLibraryItem() == libraryItem) {
                return false;
            }
        }
        itemLines.add(itemLine);
        boolean isPrintBook = (libraryItem instanceof BookItem)
                && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT;
        boolean isDiscDigital = (libraryItem instanceof DigitalItem)
                && ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC;
        if (isPrintBook || isDiscDigital) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        }
        return true;
    }

    public int countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
            return 0;
        }
        int count = 0;
        for (ItemLine itemLine : itemLines) {
            LibraryItem li = itemLine.getLibraryItem();
            if (li instanceof BookItem && ((BookItem) li).getType() == BookItemType.PRINT_FORMAT) {
                count++;
            }
        }
        return count;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (this.status == OrderStatus.COMPLETED) {
            return -1;
        }
        if (itemLine == null || !itemLines.contains(itemLine)) {
            return itemLines.size();
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        itemLines.remove(itemLine);
        if (libraryItem != null) {
            boolean isPrintBook = (libraryItem instanceof BookItem)
                    && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT;
            boolean isDiscDigital = (libraryItem instanceof DigitalItem)
                    && ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC;
            if ((isPrintBook || isDiscDigital) && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
            }
        }
        return itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine itemLine : itemLines) {
            LibraryItem li = itemLine.getLibraryItem();
            if (li == null) {
                continue;
            }
            boolean isPrintBook = (li instanceof BookItem)
                    && ((BookItem) li).getType() == BookItemType.PRINT_FORMAT;
            boolean isDiscDigital = (li instanceof DigitalItem)
                    && ((DigitalItem) li).getOption() == DigitalItemOption.DISC;
            if ((isPrintBook || isDiscDigital) && li.getStatus() == LibraryItemStatus.HOLD) {
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
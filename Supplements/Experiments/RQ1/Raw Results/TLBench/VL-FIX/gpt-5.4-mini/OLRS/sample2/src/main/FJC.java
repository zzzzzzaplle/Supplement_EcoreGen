import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        this.status = LibraryItemStatus.AVAILABLE;
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
        super();
        this.type = BookItemType.EBOOK;
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
        super();
        this.option = DigitalItemOption.DOWNLOADABLE;
        this.type = DigitalItemType.AUDIO;
    }

    public int calculateTotalDownloads(List<Order> orders) {
        if (orders == null || this.getType() != DigitalItemType.AUDIO || this.getOption() != DigitalItemOption.DOWNLOADABLE) {
            return 0;
        }

        int total = 0;
        for (Order order : orders) {
            if (order == null || order.getStatus() != OrderStatus.COMPLETED || order.getItemLines() == null) {
                continue;
            }
            for (ItemLine itemLine : order.getItemLines()) {
                if (itemLine == null) {
                    continue;
                }
                LibraryItem item = itemLine.getLibraryItem();
                if (item == this) {
                    total += itemLine.getQuantity() == null ? 0 : itemLine.getQuantity();
                }
            }
        }
        return total;
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
}

class ItemLine {
    private Integer quantity;
    private LibraryItem libraryItem;

    public ItemLine() {
        this.quantity = 0;
        this.libraryItem = null;
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
        if (itemLine == null || itemLine.getLibraryItem() == null || status != OrderStatus.PENDING) {
            return false;
        }

        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }

        if (itemLines == null) {
            itemLines = new ArrayList<>();
        }

        for (ItemLine existing : itemLines) {
            if (existing != null && existing.getLibraryItem() == libraryItem) {
                return false;
            }
        }

        if ((libraryItem instanceof BookItem && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT)
                || (libraryItem instanceof DigitalItem && ((DigitalItem) libraryItem).getType() == DigitalItemType.DISC)) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
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
            if (itemLine == null || itemLine.getLibraryItem() == null) {
                continue;
            }
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
        if (status != OrderStatus.PENDING || itemLines == null || itemLine == null || itemLine.getLibraryItem() == null) {
            return -1;
        }

        for (int i = 0; i < itemLines.size(); i++) {
            ItemLine existing = itemLines.get(i);
            if (existing != null && existing.getLibraryItem() == itemLine.getLibraryItem()) {
                LibraryItem libraryItem = existing.getLibraryItem();
                if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                    libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
                }
                itemLines.remove(i);
                return itemLines.size();
            }
        }

        return itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        if (itemLines == null) {
            return;
        }

        for (ItemLine itemLine : itemLines) {
            if (itemLine == null || itemLine.getLibraryItem() == null) {
                continue;
            }
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                if ((libraryItem instanceof BookItem && ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT)
                        || (libraryItem instanceof DigitalItem && ((DigitalItem) libraryItem).getType() == DigitalItemType.DISC)) {
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
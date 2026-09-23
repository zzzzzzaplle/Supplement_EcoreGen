import java.util.ArrayList;
import java.util.List;

enum OrderStatus {
    PENDING, COMPLETED
}

enum LibraryItemStatus {
    AVAILABLE, HOLD, LOAN
}

enum BookItemType {
    EBOOK, PRINT_FORMAT
}

enum DigitalItemOption {
    DOWNLOADABLE, DISC
}

enum DigitalItemType {
    AUDIO, VIDEO
}

abstract class LibraryItem {
    private LibraryItemStatus status;

    public LibraryItem() {}

    public LibraryItemStatus getStatus() {
        return status;
    }

    public void setStatus(LibraryItemStatus status) {
        this.status = status;
    }
}

class BookItem extends LibraryItem {
    private BookItemType type;

    public BookItem() {}

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

    public DigitalItem() {}

    public int calculateTotalDownloads(List<Order> orders) {
        if (this.getType() != DigitalItemType.AUDIO || this.getOption() != DigitalItemOption.DOWNLOADABLE) {
            return 0;
        }
        int count = 0;
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                for (ItemLine line : order.getItemLines()) {
                    if (line.getLibraryItem().equals(this)) {
                        count += line.getQuantity();
                    }
                }
            }
        }
        return count;
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

    public ItemLine() {}

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
    private List<ItemLine> itemLines = new ArrayList<>();

    public Order() {}

    public boolean addItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING || itemLine.getLibraryItem().getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        for (ItemLine line : itemLines) {
            if (line.getLibraryItem().equals(itemLine.getLibraryItem())) {
                return false;
            }
        }

        LibraryItem item = itemLine.getLibraryItem();
        boolean needsHold = (item instanceof BookItem && ((BookItem) item).getType() == BookItemType.PRINT_FORMAT) ||
                           (item instanceof DigitalItem && ((DigitalItem) item).getOption() == DigitalItemOption.DISC);

        if (needsHold) {
            item.setStatus(LibraryItemStatus.HOLD);
        }

        itemLines.add(itemLine);
        return true;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (this.status == OrderStatus.COMPLETED) {
            return -1;
        }
        if (itemLines.remove(itemLine)) {
            LibraryItem item = itemLine.getLibraryItem();
            if (item.getStatus() == LibraryItemStatus.HOLD) {
                item.setStatus(LibraryItemStatus.AVAILABLE);
            }
            return itemLines.size();
        }
        return itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine line : itemLines) {
            LibraryItem item = line.getLibraryItem();
            if (item.getStatus() == LibraryItemStatus.HOLD) {
                boolean isEligible = (item instanceof BookItem && ((BookItem) item).getType() == BookItemType.PRINT_FORMAT) ||
                                    (item instanceof DigitalItem && ((DigitalItem) item).getOption() == DigitalItemOption.DISC);
                if (isEligible) {
                    item.setStatus(LibraryItemStatus.LOAN);
                }
            }
        }
    }

    public int countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
            return 0;
        }
        int count = 0;
        for (ItemLine line : itemLines) {
            if (line.getLibraryItem() instanceof BookItem && ((BookItem) line.getLibraryItem()).getType() == BookItemType.PRINT_FORMAT) {
                count++;
            }
        }
        return count;
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
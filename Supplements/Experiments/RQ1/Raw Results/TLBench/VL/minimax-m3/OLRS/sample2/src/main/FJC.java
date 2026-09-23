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

    public Integer calculateTotalDownloads(List<Order> orders) {
        int total = 0;
        if (this.type == DigitalItemType.AUDIO && this.option == DigitalItemOption.DOWNLOADABLE) {
            for (Order order : orders) {
                if (order.getStatus() == OrderStatus.COMPLETED) {
                    for (ItemLine itemLine : order.getItemLines()) {
                        if (itemLine.getLibraryItem() == this && itemLine.getQuantity() != null) {
                            total += itemLine.getQuantity();
                        }
                    }
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
        this.itemLines = new ArrayList<>();
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
            return false;
        }

        if (itemLine == null || itemLine.getLibraryItem() == null) {
            return false;
        }

        LibraryItem item = itemLine.getLibraryItem();
        if (item.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }

        for (ItemLine existing : this.itemLines) {
            if (existing.getLibraryItem() == item) {
                return false;
            }
        }

        this.itemLines.add(itemLine);

        if (item instanceof BookItem) {
            BookItem book = (BookItem) item;
            if (book.getType() == BookItemType.PRINT_FORMAT) {
                item.setStatus(LibraryItemStatus.HOLD);
            }
        } else if (item instanceof DigitalItem) {
            DigitalItem digital = (DigitalItem) item;
            if (digital.getOption() == DigitalItemOption.DISC) {
                item.setStatus(LibraryItemStatus.HOLD);
            }
        }

        return true;
    }

    public Integer countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
            return 0;
        }

        int count = 0;
        for (ItemLine itemLine : this.itemLines) {
            LibraryItem item = itemLine.getLibraryItem();
            if (item instanceof BookItem) {
                BookItem book = (BookItem) item;
                if (book.getType() == BookItemType.PRINT_FORMAT) {
                    count++;
                }
            }
        }
        return count;
    }

    public Integer removeItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
            return -1;
        }

        if (itemLine != null && this.itemLines.remove(itemLine)) {
            LibraryItem item = itemLine.getLibraryItem();
            if (item != null && item.getStatus() == LibraryItemStatus.HOLD) {
                if (item instanceof BookItem) {
                    BookItem book = (BookItem) item;
                    if (book.getType() == BookItemType.PRINT_FORMAT) {
                        item.setStatus(LibraryItemStatus.AVAILABLE);
                    }
                } else if (item instanceof DigitalItem) {
                    DigitalItem digital = (DigitalItem) item;
                    if (digital.getOption() == DigitalItemOption.DISC) {
                        item.setStatus(LibraryItemStatus.AVAILABLE);
                    }
                }
            }
        }

        return this.itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;

        for (ItemLine itemLine : this.itemLines) {
            LibraryItem item = itemLine.getLibraryItem();
            if (item != null && item.getStatus() == LibraryItemStatus.HOLD) {
                if (item instanceof BookItem) {
                    BookItem book = (BookItem) item;
                    if (book.getType() == BookItemType.PRINT_FORMAT) {
                        item.setStatus(LibraryItemStatus.LOAN);
                    }
                } else if (item instanceof DigitalItem) {
                    DigitalItem digital = (DigitalItem) item;
                    if (digital.getOption() == DigitalItemOption.DISC) {
                        item.setStatus(LibraryItemStatus.LOAN);
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
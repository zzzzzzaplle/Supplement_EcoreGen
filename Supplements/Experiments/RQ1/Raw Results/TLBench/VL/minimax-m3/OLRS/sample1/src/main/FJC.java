import java.util.ArrayList;
import java.util.List;

enum OrderStatus {
    PENDING,
    COMPLETED
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
        if (itemLine.getLibraryItem().getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        for (ItemLine existing : this.itemLines) {
            if (existing.getLibraryItem() != null &&
                    existing.getLibraryItem().equals(itemLine.getLibraryItem())) {
                return false;
            }
        }
        this.itemLines.add(itemLine);
        LibraryItem li = itemLine.getLibraryItem();
        if (li instanceof BookItem) {
            BookItem bi = (BookItem) li;
            if (bi.getType() == BookItemType.PRINT_FORMAT) {
                bi.setStatus(LibraryItemStatus.HOLD);
            }
        } else if (li instanceof DigitalItem) {
            DigitalItem di = (DigitalItem) li;
            if (di.getOption() == DigitalItemOption.DISC) {
                di.setStatus(LibraryItemStatus.HOLD);
            }
        }
        return true;
    }

    public int countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
            return 0;
        }
        int count = 0;
        for (ItemLine il : this.itemLines) {
            LibraryItem li = il.getLibraryItem();
            if (li instanceof BookItem) {
                BookItem bi = (BookItem) li;
                if (bi.getType() == BookItemType.PRINT_FORMAT) {
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
        if (itemLine == null) {
            return this.itemLines.size();
        }
        for (int i = 0; i < this.itemLines.size(); i++) {
            ItemLine current = this.itemLines.get(i);
            if (current.getLibraryItem() != null &&
                    current.getLibraryItem().equals(itemLine.getLibraryItem())) {
                LibraryItem li = current.getLibraryItem();
                this.itemLines.remove(i);
                if (li instanceof BookItem) {
                    BookItem bi = (BookItem) li;
                    if (bi.getStatus() == LibraryItemStatus.HOLD &&
                            bi.getType() == BookItemType.PRINT_FORMAT) {
                        bi.setStatus(LibraryItemStatus.AVAILABLE);
                    }
                } else if (li instanceof DigitalItem) {
                    DigitalItem di = (DigitalItem) li;
                    if (di.getStatus() == LibraryItemStatus.HOLD &&
                            di.getOption() == DigitalItemOption.DISC) {
                        di.setStatus(LibraryItemStatus.AVAILABLE);
                    }
                }
                break;
            }
        }
        return this.itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        for (ItemLine il : this.itemLines) {
            LibraryItem li = il.getLibraryItem();
            if (li instanceof BookItem) {
                BookItem bi = (BookItem) li;
                if (bi.getType() == BookItemType.PRINT_FORMAT &&
                        bi.getStatus() == LibraryItemStatus.HOLD) {
                    bi.setStatus(LibraryItemStatus.LOAN);
                }
            } else if (li instanceof DigitalItem) {
                DigitalItem di = (DigitalItem) li;
                if (di.getOption() == DigitalItemOption.DISC &&
                        di.getStatus() == LibraryItemStatus.HOLD) {
                    di.setStatus(LibraryItemStatus.LOAN);
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

enum LibraryItemStatus {
    AVAILABLE,
    HOLD,
    LOAN
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

enum BookItemType {
    EBOOK,
    PRINT_FORMAT
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

enum DigitalItemOption {
    DOWNLOADABLE,
    DISC
}

enum DigitalItemType {
    AUDIO,
    VIDEO
}

class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
    }

    public int calculateTotalDownloads(List<Order> orders) {
        int total = 0;
        if (this.type != DigitalItemType.AUDIO || this.option != DigitalItemOption.DOWNLOADABLE) {
            return 0;
        }
        if (orders == null) {
            return 0;
        }
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                List<ItemLine> lines = order.getItemLines();
                if (lines == null) {
                    continue;
                }
                for (ItemLine il : lines) {
                    if (il.getLibraryItem() != null && il.getLibraryItem().equals(this)) {
                        if (il.getQuantity() != null) {
                            total += il.getQuantity();
                        } else {
                            total += 1;
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
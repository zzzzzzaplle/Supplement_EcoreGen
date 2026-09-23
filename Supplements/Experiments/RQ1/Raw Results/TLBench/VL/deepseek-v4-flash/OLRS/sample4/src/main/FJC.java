import java.util.List;
import java.util.ArrayList;

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
        int totalDownloads = 0;
        if (orders == null) {
            return 0;
        }
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                for (ItemLine itemLine : order.getItemLines()) {
                    LibraryItem libraryItem = itemLine.getLibraryItem();
                    if (libraryItem instanceof DigitalItem) {
                        DigitalItem digitalItem = (DigitalItem) libraryItem;
                        if (digitalItem.getType() == DigitalItemType.AUDIO && digitalItem.getOption() == DigitalItemOption.DOWNLOADABLE) {
                            totalDownloads += itemLine.getQuantity();
                        }
                    }
                }
            }
        }
        return totalDownloads;
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
        if (status != OrderStatus.PENDING) {
            return false;
        }
        if (itemLine == null || itemLine.getLibraryItem() == null) {
            return false;
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem.getStatus() == LibraryItemStatus.HOLD || libraryItem.getStatus() == LibraryItemStatus.LOAN) {
            return false;
        }
        // Check for duplicate item line (same library item)
        for (ItemLine existingLine : itemLines) {
            if (existingLine.getLibraryItem() != null && existingLine.getLibraryItem().equals(libraryItem)) {
                return false;
            }
        }
        // Modify library item status if print format book item or disc digital item
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
        // Otherwise, keep status as available (already set)
        itemLines.add(itemLine);
        return true;
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
                    count += itemLine.getQuantity();
                }
            }
        }
        return count;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING) {
            return -1;
        }
        if (itemLine == null || !itemLines.contains(itemLine)) {
            return -1;
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem != null && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
            boolean isPrintOrDisc = false;
            if (libraryItem instanceof BookItem) {
                BookItem bookItem = (BookItem) libraryItem;
                if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                    isPrintOrDisc = true;
                }
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
        itemLines.remove(itemLine);
        return itemLines.size();
    }

    public void handleOrder() {
        status = OrderStatus.COMPLETED;
        for (ItemLine itemLine : itemLines) {
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem != null && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                boolean isPrintOrDisc = false;
                if (libraryItem instanceof BookItem) {
                    BookItem bookItem = (BookItem) libraryItem;
                    if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                        isPrintOrDisc = true;
                    }
                } else if (libraryItem instanceof DigitalItem) {
                    DigitalItem digitalItem = (DigitalItem) libraryItem;
                    if (digitalItem.getOption() == DigitalItemOption.DISC) {
                        isPrintOrDisc = true;
                    }
                }
                if (isPrintOrDisc) {
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
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
        int totalDownloads = 0;
        if (orders == null) {
            return 0;
        }
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                List<ItemLine> itemLines = order.getItemLines();
                if (itemLines != null) {
                    for (ItemLine itemLine : itemLines) {
                        LibraryItem libraryItem = itemLine.getLibraryItem();
                        if (libraryItem instanceof DigitalItem) {
                            DigitalItem digitalItem = (DigitalItem) libraryItem;
                            // Only count if this is the same digital item (by reference or identity)
                            // For simplicity, we check if it's the same object using '=='
                            // In a real system, you might compare IDs
                            if (digitalItem == this && digitalItem.getOption() == DigitalItemOption.DOWNLOADABLE) {
                                totalDownloads += itemLine.getQuantity();
                            }
                        }
                    }
                }
            }
        }
        return totalDownloads;
    }
}

class ItemLine {
    private Integer quantity;
    private LibraryItem libraryItem;

    public ItemLine() {
        this.quantity = 1;
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
        // Precondition: order must be in Pending status
        if (this.status != OrderStatus.PENDING) {
            return false;
        }

        if (itemLine == null || itemLine.getLibraryItem() == null) {
            return false;
        }

        LibraryItem libraryItem = itemLine.getLibraryItem();

        // Library item must be available
        if (libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }

        // Ensure no duplicate item line (same library item) exists in the order
        for (ItemLine existingLine : this.itemLines) {
            if (existingLine.getLibraryItem() == libraryItem) {
                return false;
            }
        }

        // Determine if we need to change status to HOLD
        boolean shouldSetHold = false;
        if (libraryItem instanceof BookItem) {
            BookItem bookItem = (BookItem) libraryItem;
            if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                shouldSetHold = true;
            }
        } else if (libraryItem instanceof DigitalItem) {
            DigitalItem digitalItem = (DigitalItem) libraryItem;
            if (digitalItem.getOption() == DigitalItemOption.DISC) {
                shouldSetHold = true;
            }
        }

        if (shouldSetHold) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        }
        // Otherwise, keep status as Available (already AVAILABLE)

        this.itemLines.add(itemLine);
        return true;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (this.status != OrderStatus.PENDING) {
            return -1;
        }

        if (itemLine == null) {
            return -1;
        }

        boolean removed = this.itemLines.remove(itemLine);
        if (!removed) {
            return -1;
        }

        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem != null) {
            // Reset the library item's status to available only if it was previously Hold
            if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                // Only reset if it's a print/disc item
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
                // Otherwise, retain its original status (still HOLD)
            }
        }

        return this.itemLines.size();
    }

    public void handleOrder() {
        // First, update Order Status to "completed"
        this.status = OrderStatus.COMPLETED;

        // Then modify the library item's status from hold to loan only if the library item
        // belongs to a print format book item or disc digital item.
        for (ItemLine itemLine : this.itemLines) {
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
                    count += itemLine.getQuantity();
                }
            }
        }
        return count;
    }
}
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

    // Unparameterized constructor
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

    // Unparameterized constructor
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

    // Unparameterized constructor
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

    // Key operation: calculate total downloads for this Audio digital item
    public int calculateTotalDownloads(List<Order> orders) {
        int totalDownloads = 0;
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                for (ItemLine itemLine : order.getItemLines()) {
                    if (itemLine.getLibraryItem() == this) {
                        // Only count if option is DOWNLOADABLE
                        if (this.getOption() == DigitalItemOption.DOWNLOADABLE) {
                            totalDownloads += itemLine.getQuantity();
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

    // Unparameterized constructor
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

    // Unparameterized constructor
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

    // Key operation: add a new item line
    public boolean addItemLine(ItemLine itemLine) {
        // Precondition: order must be in Pending status
        if (this.status != OrderStatus.PENDING) {
            return false;
        }

        LibraryItem libraryItem = itemLine.getLibraryItem();
        // Precondition: library item must be available
        if (libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }

        // Ensure no duplicate item line (same library item) exists in the order
        for (ItemLine existingLine : itemLines) {
            if (existingLine.getLibraryItem() == libraryItem) {
                return false; // duplicate found
            }
        }

        // Add the item line
        itemLines.add(itemLine);

        // Modify library item status to hold only if print format book item or disc digital item
        if (libraryItem instanceof BookItem) {
            BookItem bookItem = (BookItem) libraryItem;
            if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                libraryItem.setStatus(LibraryItemStatus.HOLD);
            }
            // Otherwise keep as available (already available)
        } else if (libraryItem instanceof DigitalItem) {
            DigitalItem digitalItem = (DigitalItem) libraryItem;
            if (digitalItem.getOption() == DigitalItemOption.DISC) {
                libraryItem.setStatus(LibraryItemStatus.HOLD);
            }
            // Otherwise keep as available
        }
        // For any other LibraryItem (though only these subclasses exist), keep as available

        return true;
    }

    // Key operation: remove an item line from an existing order in Pending status
    public int removeItemLine(ItemLine itemLine) {
        // Check if order is pending
        if (this.status != OrderStatus.PENDING) {
            return -1; // failure, order is completed
        }

        // Find and remove the item line
        boolean removed = itemLines.remove(itemLine);
        if (!removed) {
            return -1; // item line not found
        }

        // Reset library item status to available only if it was previously Hold (for print/disc items)
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
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
            // Otherwise retain its original status (still HOLD)
        }

        // Return updated count of item lines
        return itemLines.size();
    }

    // Key operation: handle an existing order
    public void handleOrder() {
        // Update Order Status to "completed"
        this.status = OrderStatus.COMPLETED;

        // Modify library item status from hold to loan only if print format book item or disc digital item
        for (ItemLine itemLine : itemLines) {
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
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
                // Otherwise, keep status as hold (no change)
            }
        }
    }

    // Key operation: count total number of print-format book items in a given order only if completed
    public int countPrintBookItemIfCompleted() {
        if (this.status != OrderStatus.COMPLETED) {
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
}
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the status of an order.
 */
enum OrderStatus {
    PENDING,
    COMPLETED
}

/**
 * Represents the status of a library item.
 */
enum LibraryItemStatus {
    AVAILABLE,
    HOLD,
    LOAN
}

/**
 * Represents the type of a book item.
 */
enum BookItemType {
    EBOOK,
    PRINT_FORMAT
}

/**
 * Represents the option of a digital item.
 */
enum DigitalItemOption {
    DOWNLOADABLE,
    DISC
}

/**
 * Represents the type of a digital item.
 */
enum DigitalItemType {
    AUDIO,
    VIDEO
}

/**
 * Abstract base class for all library items.
 */
abstract class LibraryItem {
    private LibraryItemStatus status;

    /**
     * Creates a library item with default values.
     */
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

/**
 * Represents a book item in the library.
 */
class BookItem extends LibraryItem {
    private BookItemType type;

    /**
     * Creates a book item with default values.
     */
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

/**
 * Represents a digital item in the library.
 */
class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    /**
     * Creates a digital item with default values.
     */
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

    /**
     * Calculates total downloads for this audio downloadable digital item
     * across completed orders.
     *
     * @param orders list of orders to evaluate
     * @return total downloads count
     */
    public int calculateTotalDownloads(List<Order> orders) {
        if (orders == null || option != DigitalItemOption.DOWNLOADABLE || type != DigitalItemType.AUDIO) {
            return 0;
        }

        int total = 0;
        for (Order order : orders) {
            if (order == null || order.getStatus() != OrderStatus.COMPLETED || order.getItemLines() == null) {
                continue;
            }
            for (ItemLine itemLine : order.getItemLines()) {
                if (itemLine == null || itemLine.getLibraryItem() == null) {
                    continue;
                }
                if (itemLine.getLibraryItem() == this) {
                    Integer qty = itemLine.getQuantity();
                    total += qty == null ? 0 : qty;
                }
            }
        }
        return total;
    }
}

/**
 * Represents one line item inside an order.
 */
class ItemLine {
    private Integer quantity;
    private LibraryItem libraryItem;

    /**
     * Creates an item line with default values.
     */
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

/**
 * Represents an order consisting of multiple item lines.
 */
class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    /**
     * Creates an order with default values.
     */
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

    /**
     * Adds an item line to this order if validation succeeds.
     *
     * Preconditions:
     * - Order must be PENDING
     * - Library item must be AVAILABLE
     * - No duplicate item line with same library item exists
     * - If book is PRINT_FORMAT or digital item is DISC, item status becomes HOLD
     *   otherwise remains AVAILABLE
     *
     * @param itemLine item line to add
     * @return true if added successfully, false otherwise
     */
    public boolean addItemLine(ItemLine itemLine) {
        if (itemLine == null || status != OrderStatus.PENDING || itemLines == null) {
            return false;
        }

        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem == null || libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }

        for (ItemLine existing : itemLines) {
            if (existing != null && existing.getLibraryItem() == libraryItem) {
                return false;
            }
        }

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

        itemLines.add(itemLine);
        return true;
    }

    /**
     * Removes an item line from a pending order.
     *
     * Rules:
     * - If order is COMPLETED, return -1
     * - If removed successfully, return updated item line count
     * - Reset library item status to AVAILABLE only if it was previously HOLD
     *
     * @param itemLine item line to remove
     * @return updated count or -1 if order is completed
     */
    public int removeItemLine(ItemLine itemLine) {
        if (status == OrderStatus.COMPLETED) {
            return -1;
        }
        if (itemLines == null || itemLine == null) {
            return 0;
        }

        for (int i = 0; i < itemLines.size(); i++) {
            ItemLine existing = itemLines.get(i);
            if (existing != null && existing.getLibraryItem() == itemLine.getLibraryItem()) {
                LibraryItem libraryItem = existing.getLibraryItem();
                if (libraryItem != null && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                    libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
                }
                itemLines.remove(i);
                return itemLines.size();
            }
        }
        return itemLines.size();
    }

    /**
     * Completes the order and updates the statuses of eligible library items.
     *
     * For print-format book items and disc digital items:
     * - HOLD -> LOAN
     */
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
            if (libraryItem.getStatus() != LibraryItemStatus.HOLD) {
                continue;
            }

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

    /**
     * Counts print-format book item lines only if the order is completed.
     *
     * @return total count of print-format book items; 0 if order is not completed or none found
     */
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
                    Integer qty = itemLine.getQuantity();
                    count += qty == null ? 0 : qty;
                }
            }
        }
        return count;
    }
}
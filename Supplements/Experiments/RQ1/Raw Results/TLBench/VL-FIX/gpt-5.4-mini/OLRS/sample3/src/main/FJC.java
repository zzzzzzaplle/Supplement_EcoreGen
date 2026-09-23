import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the status of an order in the Online Library System.
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
     * No-argument constructor.
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
     * No-argument constructor.
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
     * No-argument constructor.
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
     * Calculates the total downloads for this audio digital item across completed orders.
     * Only downloadable audio items are counted.
     *
     * @param orders list of orders to inspect
     * @return total number of downloads
     */
    public int calculateTotalDownloads(List<Order> orders) {
        if (orders == null || this.type != DigitalItemType.AUDIO || this.option != DigitalItemOption.DOWNLOADABLE) {
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
                    total += safeQuantity(itemLine.getQuantity());
                }
            }
        }
        return total;
    }

    private int safeQuantity(Integer quantity) {
        return quantity == null ? 0 : quantity;
    }
}

/**
 * Represents an item line within an order.
 */
class ItemLine {
    private Integer quantity;
    private LibraryItem libraryItem;

    /**
     * No-argument constructor.
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
 * Represents a customer order in the Online Library System.
 */
class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    /**
     * No-argument constructor.
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
     * Adds an item line to the order.
     * Preconditions:
     * - Order must be pending.
     * - Library item must be available.
     * - No duplicate library item can exist in the order.
     * - Library item status changes from available to hold only for print-format book items or disc digital items.
     *
     * @param itemLine the item line to add
     * @return true if added successfully; false otherwise
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
     * Removes an item line from the order if the order is pending.
     * If the removed library item was previously on hold and is a print/disc item, its status is reset to available.
     *
     * @param itemLine the item line to remove
     * @return updated count of item lines if successful; -1 if the order is completed
     */
    public int removeItemLine(ItemLine itemLine) {
        if (status == OrderStatus.COMPLETED) {
            return -1;
        }
        if (itemLine == null || itemLines == null) {
            return itemLines == null ? 0 : itemLines.size();
        }

        int index = -1;
        for (int i = 0; i < itemLines.size(); i++) {
            if (itemLines.get(i) == itemLine) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return itemLines.size();
        }

        LibraryItem libraryItem = itemLines.get(index).getLibraryItem();
        if (libraryItem != null && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
            if (libraryItem instanceof BookItem) {
                BookItem bookItem = (BookItem) libraryItem;
                if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                    libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
                }
            } else if (libraryItem instanceof DigitalItem) {
                DigitalItem digitalItem = (DigitalItem) libraryItem;
                if (digitalItem.getOption() == DigitalItemOption.DISC) {
                    libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
                }
            }
        }

        itemLines.remove(index);
        return itemLines.size();
    }

    /**
     * Handles the order by marking it completed and updating held print/disc items to loan.
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
            if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
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
    }

    /**
     * Counts the total number of print-format book items in the order only if completed.
     *
     * @return number of print-format book items, or 0 if not completed or none exist
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
                    count += safeQuantity(itemLine.getQuantity());
                }
            }
        }
        return count;
    }

    private int safeQuantity(Integer quantity) {
        return quantity == null ? 0 : quantity;
    }
}
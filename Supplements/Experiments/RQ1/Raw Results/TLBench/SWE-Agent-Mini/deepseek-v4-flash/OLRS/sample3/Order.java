import java.util.ArrayList;
import java.util.List;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
    }

    public boolean addItemLine(ItemLine itemLine) {
        // Precondition 1: Order must be in Pending status
        if (status != OrderStatus.PENDING) {
            return false;
        }

        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem == null) {
            return false;
        }

        // Precondition 2: Library item must be available (not Hold or Loan)
        if (libraryItem.getStatus() == LibraryItemStatus.HOLD || 
            libraryItem.getStatus() == LibraryItemStatus.LOAN) {
            return false;
        }

        // Precondition 3: Ensure no duplicate item line (same library item) exists
        if (itemLines != null) {
            for (ItemLine existingLine : itemLines) {
                if (existingLine.getLibraryItem() != null && 
                    existingLine.getLibraryItem().equals(libraryItem)) {
                    return false;
                }
                // Also check by reference if equals not overridden
                if (existingLine.getLibraryItem() == libraryItem) {
                    return false;
                }
            }
        }

        // Modify library item status if applicable
        boolean shouldSetHold = false;

        // Check if the library item is a print format book item
        if (libraryItem instanceof BookItem) {
            BookItem bookItem = (BookItem) libraryItem;
            if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                shouldSetHold = true;
            }
        }
        // Check if the library item is a disc digital item
        else if (libraryItem instanceof DigitalItem) {
            DigitalItem digitalItem = (DigitalItem) libraryItem;
            if (digitalItem.getOption() == DigitalItemOption.DISC) {
                shouldSetHold = true;
            }
        }

        if (shouldSetHold) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        }
        // Otherwise, keep the status as Available (it's already available)

        // Add the item line
        if (itemLines == null) {
            itemLines = new ArrayList<>();
        }
        itemLines.add(itemLine);

        return true;
    }

    public int removeItemLine(ItemLine itemLine) {
        // Check if order is in Pending status
        if (status != OrderStatus.PENDING) {
            return -1;
        }

        if (itemLines == null) {
            return 0;
        }

        // Find and remove the item line
        boolean removed = false;
        LibraryItem libraryItem = itemLine.getLibraryItem();
        
        for (int i = 0; i < itemLines.size(); i++) {
            ItemLine existingLine = itemLines.get(i);
            if (existingLine.getLibraryItem() != null && 
                existingLine.getLibraryItem().equals(libraryItem)) {
                itemLines.remove(i);
                removed = true;
                break;
            }
            if (existingLine.getLibraryItem() == libraryItem) {
                itemLines.remove(i);
                removed = true;
                break;
            }
        }

        if (removed) {
            // Reset the library item's status to available only if it was previously Hold
            // and it's a print/disc item
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
                // Otherwise retain its original status (HOLD)
            }
            // If status was not Hold, retain its original status
        }

        return itemLines.size();
    }

    public void handleOrder() {
        // First, update Order Status to "completed"
        this.status = OrderStatus.COMPLETED;

        // Then modify the library item's status from hold to loan
        // only if the library item belongs to a print format book item or disc digital item
        if (itemLines != null) {
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
    }

    public int countPrintBookItemIfCompleted() {
        if (status != OrderStatus.COMPLETED) {
            return 0;
        }
        int count = 0;
        if (itemLines != null) {
            for (ItemLine itemLine : itemLines) {
                LibraryItem libraryItem = itemLine.getLibraryItem();
                if (libraryItem instanceof BookItem) {
                    BookItem bookItem = (BookItem) libraryItem;
                    if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                        count++;
                    }
                }
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

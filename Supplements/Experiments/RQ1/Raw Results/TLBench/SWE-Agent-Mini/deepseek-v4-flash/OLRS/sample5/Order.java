import java.util.List;
import java.util.ArrayList;

public class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;

    public Order() {
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
        // Precondition: The order must be in Pending status
        if (status != OrderStatus.PENDING) {
            return false;
        }

        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem == null) {
            return false;
        }

        // The library item must be available (not Hold or Loan)
        if (libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }

        // Ensure no duplicate item line (same library item) exists in the order
        if (itemLines != null) {
            for (ItemLine existingItemLine : itemLines) {
                if (existingItemLine.getLibraryItem() != null && existingItemLine.getLibraryItem() == libraryItem) {
                    return false;
                }
            }
        }

        // Add the item line
        if (itemLines == null) {
            itemLines = new ArrayList<ItemLine>();
        }
        itemLines.add(itemLine);

        // Modify the library item's status from available to hold only if the library item
        // is a print format book item or disc digital item
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
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        } else {
            libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
        }

        return true;
    }

    public int removeItemLine(ItemLine itemLine) {
        // Only for existing order in Pending status
        if (status != OrderStatus.PENDING) {
            return -1;
        }

        if (itemLines == null) {
            return -1;
        }

        // Find and remove the item line
        boolean removed = false;
        LibraryItem libraryItem = itemLine.getLibraryItem();
        
        for (int i = 0; i < itemLines.size(); i++) {
            if (itemLines.get(i) == itemLine || 
                (itemLines.get(i).getLibraryItem() != null && itemLines.get(i).getLibraryItem() == libraryItem)) {
                itemLines.remove(i);
                removed = true;
                break;
            }
        }

        if (!removed) {
            return -1;
        }

        // Reset the library item's status to available only if it was previously Hold (for print/disc items)
        if (libraryItem != null) {
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

            if (isPrintOrDisc && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
            }
            // Otherwise, retain its original status
        }

        return itemLines.size();
    }

    public void handleOrder() {
        // First, update Order Status to "completed"
        status = OrderStatus.COMPLETED;

        // Then modify the library item's status from hold to loan
        // only if the library item belongs to a print format book item or disc digital item
        if (itemLines != null) {
            for (ItemLine itemLine : itemLines) {
                LibraryItem libraryItem = itemLine.getLibraryItem();
                if (libraryItem != null) {
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

                    if (isPrintOrDisc && libraryItem.getStatus() == LibraryItemStatus.HOLD) {
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
}

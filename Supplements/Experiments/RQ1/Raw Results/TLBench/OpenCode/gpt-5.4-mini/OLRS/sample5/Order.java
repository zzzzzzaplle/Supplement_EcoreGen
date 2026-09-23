class Order {
    private OrderStatus status;
    private java.util.List<ItemLine> itemLines;

    public Order() {
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (itemLine == null || status != OrderStatus.PENDING) {
            return false;
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem == null || libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        if (itemLines == null) {
            itemLines = new java.util.ArrayList<ItemLine>();
        }
        for (ItemLine existingItemLine : itemLines) {
            if (existingItemLine != null && existingItemLine.getLibraryItem() == libraryItem) {
                return false;
            }
        }
        itemLines.add(itemLine);
        if (isHoldRequired(libraryItem)) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        }
        return true;
    }

    public int countPrintBookItemIfCompleted() {
        if (status != OrderStatus.COMPLETED || itemLines == null) {
            return 0;
        }
        int count = 0;
        for (ItemLine itemLine : itemLines) {
            if (itemLine == null) {
                continue;
            }
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem instanceof BookItem) {
                BookItem bookItem = (BookItem) libraryItem;
                if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                    count += itemLine.getQuantity() == null ? 0 : itemLine.getQuantity();
                }
            }
        }
        return count;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (status == OrderStatus.COMPLETED || itemLine == null || itemLines == null) {
            return -1;
        }
        for (int i = 0; i < itemLines.size(); i++) {
            ItemLine existingItemLine = itemLines.get(i);
            if (existingItemLine != null && existingItemLine.getLibraryItem() == itemLine.getLibraryItem()) {
                LibraryItem libraryItem = existingItemLine.getLibraryItem();
                if (libraryItem != null && libraryItem.getStatus() == LibraryItemStatus.HOLD && isHoldRequired(libraryItem)) {
                    libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
                }
                itemLines.remove(i);
                return itemLines.size();
            }
        }
        return itemLines.size();
    }

    public void handleOrder() {
        status = OrderStatus.COMPLETED;
        if (itemLines == null) {
            return;
        }
        for (ItemLine itemLine : itemLines) {
            if (itemLine == null) {
                continue;
            }
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem != null && libraryItem.getStatus() == LibraryItemStatus.HOLD && isHoldRequired(libraryItem)) {
                libraryItem.setStatus(LibraryItemStatus.LOAN);
            }
        }
    }

    private boolean isHoldRequired(LibraryItem libraryItem) {
        if (libraryItem instanceof BookItem) {
            return ((BookItem) libraryItem).getType() == BookItemType.PRINT_FORMAT;
        }
        if (libraryItem instanceof DigitalItem) {
            return ((DigitalItem) libraryItem).getOption() == DigitalItemOption.DISC;
        }
        return false;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public java.util.List<ItemLine> getItemLines() {
        return itemLines;
    }

    public void setItemLines(java.util.List<ItemLine> itemLines) {
        this.itemLines = itemLines;
    }
}

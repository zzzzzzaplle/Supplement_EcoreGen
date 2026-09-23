class Order {
    private OrderStatus status;
    private java.util.List<ItemLine> itemLines;

    public Order() {
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING || itemLine == null || itemLine.getLibraryItem() == null) {
            return false;
        }
        if (itemLines == null) {
            itemLines = new java.util.ArrayList<ItemLine>();
        }
        for (ItemLine existing : itemLines) {
            if (existing != null && existing.getLibraryItem() == itemLine.getLibraryItem()) {
                return false;
            }
        }
        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        if (isHoldRequired(libraryItem)) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        }
        itemLines.add(itemLine);
        return true;
    }

    public int countPrintBookItemIfCompleted() {
        if (status != OrderStatus.COMPLETED || itemLines == null) {
            return 0;
        }
        int count = 0;
        for (ItemLine itemLine : itemLines) {
            if (itemLine != null && itemLine.getLibraryItem() instanceof BookItem) {
                BookItem bookItem = (BookItem) itemLine.getLibraryItem();
                if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                    Integer quantity = itemLine.getQuantity();
                    if (quantity != null) {
                        count += quantity;
                    }
                }
            }
        }
        return count;
    }

    public int removeItemLine(ItemLine itemLine) {
        if (status == OrderStatus.COMPLETED || itemLines == null || itemLine == null) {
            return -1;
        }
        for (int i = 0; i < itemLines.size(); i++) {
            ItemLine existing = itemLines.get(i);
            if (existing != null && existing.getLibraryItem() == itemLine.getLibraryItem()) {
                LibraryItem libraryItem = existing.getLibraryItem();
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
            if (itemLine == null || itemLine.getLibraryItem() == null) {
                continue;
            }
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem.getStatus() == LibraryItemStatus.HOLD && isHoldRequired(libraryItem)) {
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

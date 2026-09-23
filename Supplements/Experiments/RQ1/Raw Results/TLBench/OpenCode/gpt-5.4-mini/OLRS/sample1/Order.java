class Order {
    private OrderStatus status;
    private java.util.List<ItemLine> itemLines;

    public Order() {
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (itemLine == null || status != OrderStatus.PENDING || itemLine.getLibraryItem() == null) {
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
        itemLines.add(itemLine);
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
        return true;
    }

    public Integer countPrintBookItemIfCompleted() {
        if (status != OrderStatus.COMPLETED || itemLines == null) {
            return 0;
        }
        int count = 0;
        for (ItemLine itemLine : itemLines) {
            if (itemLine != null && itemLine.getLibraryItem() instanceof BookItem) {
                BookItem bookItem = (BookItem) itemLine.getLibraryItem();
                if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                    count += itemLine.getQuantity() == null ? 0 : itemLine.getQuantity();
                }
            }
        }
        return count;
    }

    public Integer removeItemLine(ItemLine itemLine) {
        if (status == OrderStatus.COMPLETED || itemLines == null || itemLine == null) {
            return -1;
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

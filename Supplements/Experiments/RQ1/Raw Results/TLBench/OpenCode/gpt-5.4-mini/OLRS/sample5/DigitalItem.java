class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
    }

    public int calculateTotalDownloads(java.util.List<Order> orders) {
        if (orders == null) {
            return 0;
        }
        int total = 0;
        for (Order order : orders) {
            if (order == null || order.getStatus() != OrderStatus.COMPLETED) {
                continue;
            }
            java.util.List<ItemLine> itemLines = order.getItemLines();
            if (itemLines == null) {
                continue;
            }
            for (ItemLine itemLine : itemLines) {
                if (itemLine == null) {
                    continue;
                }
                LibraryItem libraryItem = itemLine.getLibraryItem();
                if (libraryItem instanceof DigitalItem) {
                    DigitalItem digitalItem = (DigitalItem) libraryItem;
                    if (digitalItem.getType() == DigitalItemType.AUDIO && digitalItem.getOption() == DigitalItemOption.DOWNLOADABLE) {
                        total += itemLine.getQuantity() == null ? 0 : itemLine.getQuantity();
                    }
                }
            }
        }
        return total;
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

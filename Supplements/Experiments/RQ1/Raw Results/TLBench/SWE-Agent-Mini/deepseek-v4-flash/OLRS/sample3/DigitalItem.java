public class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
    }

    public int calculateTotalDownloads(java.util.List<Order> orders) {
        int totalDownloads = 0;
        if (orders == null) {
            return 0;
        }
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                java.util.List<ItemLine> itemLines = order.getItemLines();
                if (itemLines != null) {
                    for (ItemLine itemLine : itemLines) {
                        LibraryItem libraryItem = itemLine.getLibraryItem();
                        if (libraryItem instanceof DigitalItem) {
                            DigitalItem digitalItem = (DigitalItem) libraryItem;
                            if (digitalItem.getOption() == DigitalItemOption.DOWNLOADABLE && 
                                digitalItem.getType() == DigitalItemType.AUDIO) {
                                totalDownloads++;
                            }
                        }
                    }
                }
            }
        }
        return totalDownloads;
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

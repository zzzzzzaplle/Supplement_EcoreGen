import java.util.List;

public class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
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

    public int calculateTotalDownloads(List<Order> orders) {
        int total = 0;
        // Only consider orders with COMPLETED status
        if (orders == null) {
            return 0;
        }
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                List<ItemLine> itemLines = order.getItemLines();
                if (itemLines != null) {
                    for (ItemLine itemLine : itemLines) {
                        // Check if the item is a DigitalItem of AUDIO type
                        LibraryItem libItem = itemLine.getLibraryItem();
                        if (libItem instanceof DigitalItem) {
                            DigitalItem digitalItem = (DigitalItem) libItem;
                            // Only count if the digital item's option is DOWNLOADABLE (not DISC)
                            if (digitalItem.getOption() == DigitalItemOption.DOWNLOADABLE) {
                                // Only count AUDIO digital items
                                if (digitalItem.getType() == DigitalItemType.AUDIO) {
                                    total += itemLine.getQuantity();
                                }
                            }
                        }
                    }
                }
            }
        }
        return total;
    }
}

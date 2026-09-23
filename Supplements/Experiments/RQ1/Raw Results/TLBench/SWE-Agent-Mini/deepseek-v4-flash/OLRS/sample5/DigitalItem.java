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
        int totalDownloads = 0;
        if (orders == null) {
            return 0;
        }
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                List<ItemLine> itemLines = order.getItemLines();
                if (itemLines != null) {
                    for (ItemLine itemLine : itemLines) {
                        LibraryItem libItem = itemLine.getLibraryItem();
                        if (libItem instanceof DigitalItem) {
                            DigitalItem digitalItem = (DigitalItem) libItem;
                            if (digitalItem.getType() == DigitalItemType.AUDIO && digitalItem.getOption() == DigitalItemOption.DOWNLOADABLE) {
                                if (digitalItem == this) {
                                    totalDownloads += itemLine.getQuantity();
                                }
                            }
                        }
                    }
                }
            }
        }
        return totalDownloads;
    }
}

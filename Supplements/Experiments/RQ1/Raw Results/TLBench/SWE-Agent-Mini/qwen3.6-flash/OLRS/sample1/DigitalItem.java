import java.util.List;

public class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
    }

    public int calculateTotalDownloads(List<Order> orders) {
        int total = 0;
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                for (ItemLine itemLine : order.getItemLines()) {
                    LibraryItem libItem = itemLine.getLibraryItem();
                    if (libItem instanceof DigitalItem) {
                        DigitalItem digitalItem = (DigitalItem) libItem;
                        if (digitalItem.getType() == DigitalItemType.AUDIO && digitalItem.getOption() == DigitalItemOption.DOWNLOADABLE) {
                            total += itemLine.getQuantity();
                        }
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

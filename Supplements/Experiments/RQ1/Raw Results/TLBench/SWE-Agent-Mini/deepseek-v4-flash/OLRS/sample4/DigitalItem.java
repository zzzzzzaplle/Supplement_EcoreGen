import java.util.List;

public class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
    }

    public int calculateTotalDownloads(List<Order> orders) {
        if (type != DigitalItemType.AUDIO || option != DigitalItemOption.DOWNLOADABLE) {
            return 0;
        }
        int totalDownloads = 0;
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                for (ItemLine itemLine : order.getItemLines()) {
                    LibraryItem libItem = itemLine.getLibraryItem();
                    if (libItem == this) {
                        totalDownloads += itemLine.getQuantity();
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

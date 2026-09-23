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
                if (this.getType() == DigitalItemType.AUDIO && this.getOption() == DigitalItemOption.DOWNLOADABLE) {
                    for (ItemLine itemLine : order.getItemLines()) {
                        if (itemLine != null && itemLine.getLibraryItem() == this) {
                            totalDownloads += itemLine.getQuantity();
                        }
                    }
                }
            }
        }
        return totalDownloads;
    }
}

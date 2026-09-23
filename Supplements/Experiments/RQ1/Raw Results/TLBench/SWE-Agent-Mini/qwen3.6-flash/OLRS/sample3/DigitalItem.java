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
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                for (ItemLine itemLine : order.getItemLines()) {
                    LibraryItem item = itemLine.getLibraryItem();
                    if (item instanceof DigitalItem) {
                        DigitalItem digitalItem = (DigitalItem) item;
                        if (digitalItem.getOption() == DigitalItemOption.DOWNLOADABLE) {
                            // Assuming the quantity represents downloads for downloadable items
                            totalDownloads += itemLine.getQuantity();
                        }
                    }
                }
            }
        }
        return totalDownloads;
    }
}

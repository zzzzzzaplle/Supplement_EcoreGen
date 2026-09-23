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
                    LibraryItem libraryItem = itemLine.getLibraryItem();
                    if (libraryItem instanceof DigitalItem) {
                        DigitalItem digitalItem = (DigitalItem) libraryItem;
                        if (digitalItem.getOption() == DigitalItemOption.DOWNLOADABLE) {
                            totalDownloads += itemLine.getQuantity();
                        }
                    }
                }
            }
        }
        return totalDownloads;
    }
}

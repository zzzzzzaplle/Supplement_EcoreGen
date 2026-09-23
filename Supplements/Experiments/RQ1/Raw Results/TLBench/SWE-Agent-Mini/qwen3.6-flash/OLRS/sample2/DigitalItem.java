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
        if (orders != null) {
            for (Order order : orders) {
                if (order.getStatus() == OrderStatus.COMPLETED) {
                    List<ItemLine> itemLines = order.getItemLines();
                    if (itemLines != null) {
                        for (ItemLine itemLine : itemLines) {
                            LibraryItem li = itemLine.getLibraryItem();
                            if (li instanceof DigitalItem) {
                                DigitalItem digitalItem = (DigitalItem) li;
                                if (this.equals(digitalItem)) {
                                    if (digitalItem.getType() == DigitalItemType.AUDIO && digitalItem.getOption() == DigitalItemOption.DOWNLOADABLE) {
                                        total += (itemLine.getQuantity() != null ? itemLine.getQuantity() : 0);
                                    }
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

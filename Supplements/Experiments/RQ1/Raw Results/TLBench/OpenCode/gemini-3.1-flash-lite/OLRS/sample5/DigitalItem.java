import java.util.List;

public class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {}

    public int calculateTotalDownloads(List<Order> orders) {
        if (this.type != DigitalItemType.AUDIO || this.option != DigitalItemOption.DOWNLOADABLE) return 0;
        int total = 0;
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                for (ItemLine line : order.getItemLines()) {
                    if (line.getLibraryItem() == this) {
                        total += line.getQuantity();
                    }
                }
            }
        }
        return total;
    }

    public DigitalItemOption getOption() { return option; }
    public void setOption(DigitalItemOption option) { this.option = option; }
    public DigitalItemType getType() { return type; }
    public void setType(DigitalItemType type) { this.type = type; }
}

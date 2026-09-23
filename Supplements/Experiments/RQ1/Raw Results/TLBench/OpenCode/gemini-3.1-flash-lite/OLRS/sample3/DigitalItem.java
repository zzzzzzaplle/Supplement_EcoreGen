import java.util.List;
public class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;
    public DigitalItem() {}
    public DigitalItemOption getOption() { return option; }
    public void setOption(DigitalItemOption option) { this.option = option; }
    public DigitalItemType getType() { return type; }
    public void setType(DigitalItemType type) { this.type = type; }
    public Integer calculateTotalDownloads(List<Order> orders) {
        int count = 0;
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                for (ItemLine line : order.getItemLines()) {
                    if (line.getLibraryItem() == this && this.getOption() == DigitalItemOption.DOWNLOADABLE) {
                        count += line.getQuantity();
                    }
                }
            }
        }
        return count;
    }
}

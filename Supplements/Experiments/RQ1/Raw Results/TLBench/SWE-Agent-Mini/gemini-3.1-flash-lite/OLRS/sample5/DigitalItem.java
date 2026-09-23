import java.util.List;

public class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {}

    public int calculateTotalDownloads(List<Order> orders) {
        if (this.getType() != DigitalItemType.AUDIO || this.getOption() != DigitalItemOption.DOWNLOADABLE) {
            return 0;
        }
        int total = 0;
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.COMPLETED) {
                for (ItemLine il : o.getItemLines()) {
                    if (il.getLibraryItem() == this) {
                        total += il.getQuantity();
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

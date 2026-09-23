import java.util.List;

public class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
    }

    public int calculateTotalDownloads(List<Order> orders) {
        int total = 0;
        if (orders == null) {
            return total;
        }
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.COMPLETED && option == DigitalItemOption.DOWNLOADABLE) {
                for (ItemLine itemLine : order.getItemLines()) {
                    if (itemLine.getLibraryItem() == this) {
                        total += itemLine.getQuantity();
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

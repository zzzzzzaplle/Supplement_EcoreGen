import java.util.List;

public class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
    }

    public int calculateTotalDownloads(List<Order> orders) {
        int total = 0;
        if (orders == null) {
            return 0;
        }
        for (Order order : orders) {
            if (order == null || order.getStatus() != OrderStatus.COMPLETED) {
                continue;
            }
            if (order.getItemLines() == null) {
                continue;
            }
            for (ItemLine line : order.getItemLines()) {
                if (line == null || line.getLibraryItem() != this) {
                    continue;
                }
                if (this.getType() == DigitalItemType.AUDIO && this.getOption() == DigitalItemOption.DOWNLOADABLE) {
                    total += line.getQuantity() == null ? 0 : line.getQuantity();
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

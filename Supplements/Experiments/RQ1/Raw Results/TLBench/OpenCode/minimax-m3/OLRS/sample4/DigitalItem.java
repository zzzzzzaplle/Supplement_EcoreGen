import java.util.List;

public class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
        super();
    }

    public Integer calculateTotalDownloads(List<Order> orders) {
        if (this.option != DigitalItemOption.DOWNLOADABLE) {
            return 0;
        }
        if (this.type != DigitalItemType.AUDIO) {
            return 0;
        }
        if (orders == null || orders.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (Order order : orders) {
            if (order == null) {
                continue;
            }
            if (order.getStatus() != OrderStatus.COMPLETED) {
                continue;
            }
            List<ItemLine> lines = order.getItemLines();
            if (lines == null) {
                continue;
            }
            for (ItemLine line : lines) {
                if (line == null) {
                    continue;
                }
                if (line.getLibraryItem() == this) {
                    Integer qty = line.getQuantity();
                    if (qty != null) {
                        total += qty;
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

import java.util.List;

public class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
        super();
    }

    public int calculateTotalDownloads(List<Order> orders) {
        if (this.option != DigitalItemOption.DOWNLOADABLE) {
            return 0;
        }
        int total = 0;
        if (orders == null) {
            return 0;
        }
        for (Order order : orders) {
            if (order == null || order.getStatus() != OrderStatus.COMPLETED) {
                continue;
            }
            List<ItemLine> lines = order.getItemLines();
            if (lines == null) {
                continue;
            }
            for (ItemLine line : lines) {
                if (line != null && line.getLibraryItem() == this) {
                    Integer q = line.getQuantity();
                    if (q != null) {
                        total += q;
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

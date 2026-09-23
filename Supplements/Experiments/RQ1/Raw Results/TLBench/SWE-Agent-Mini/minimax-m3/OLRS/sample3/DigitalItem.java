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
        if (this.getType() != DigitalItemType.AUDIO) {
            return 0;
        }
        if (this.getOption() != DigitalItemOption.DOWNLOADABLE) {
            return 0;
        }
        if (orders == null) {
            return 0;
        }
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
                    if (line.getQuantity() != null) {
                        total += line.getQuantity();
                    }
                }
            }
        }
        return total;
    }
}

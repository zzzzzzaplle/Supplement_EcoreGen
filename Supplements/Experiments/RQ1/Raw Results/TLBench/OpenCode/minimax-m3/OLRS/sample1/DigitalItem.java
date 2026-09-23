import java.util.List;

public class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
        super();
        this.option = DigitalItemOption.DOWNLOADABLE;
        this.type = DigitalItemType.AUDIO;
    }

    public int calculateTotalDownloads(List<Order> orders) {
        if (this.option != DigitalItemOption.DOWNLOADABLE) {
            return 0;
        }
        int totalDownloads = 0;
        if (orders == null) {
            return 0;
        }
        for (Order order : orders) {
            if (order == null || order.getStatus() != OrderStatus.COMPLETED) {
                continue;
            }
            List<ItemLine> itemLines = order.getItemLines();
            if (itemLines == null) {
                continue;
            }
            for (ItemLine line : itemLines) {
                if (line == null) {
                    continue;
                }
                LibraryItem item = line.getLibraryItem();
                if (item == this) {
                    Integer qty = line.getQuantity();
                    if (qty != null) {
                        totalDownloads += qty;
                    }
                }
            }
        }
        return totalDownloads;
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

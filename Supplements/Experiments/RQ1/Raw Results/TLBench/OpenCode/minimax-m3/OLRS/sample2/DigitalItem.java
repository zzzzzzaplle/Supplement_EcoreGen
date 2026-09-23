import java.util.List;

public class DigitalItem extends LibraryItem {

    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
        super();
    }

    public int calculateTotalDownloads(List<Order> orders) {
        int total = 0;
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
            if (this.getOption() != DigitalItemOption.DOWNLOADABLE) {
                continue;
            }
            if (this.getType() != DigitalItemType.AUDIO) {
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
                if (item instanceof DigitalItem) {
                    DigitalItem di = (DigitalItem) item;
                    if (di == this
                            && di.getType() == DigitalItemType.AUDIO
                            && di.getOption() == DigitalItemOption.DOWNLOADABLE) {
                        Integer q = line.getQuantity();
                        if (q != null) {
                            total += q;
                        }
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

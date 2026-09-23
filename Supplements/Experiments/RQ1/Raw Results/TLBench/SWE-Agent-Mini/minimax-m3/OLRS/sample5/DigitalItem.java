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

    public Integer calculateTotalDownloads(java.util.List<Order> orders) {
        int total = 0;
        if (this.type != DigitalItemType.AUDIO) {
            return 0;
        }
        if (this.option != DigitalItemOption.DOWNLOADABLE) {
            return 0;
        }
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
}

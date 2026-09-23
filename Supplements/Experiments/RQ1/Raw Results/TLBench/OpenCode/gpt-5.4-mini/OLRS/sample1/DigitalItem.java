class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
    }

    public Integer calculateTotalDownloads(java.util.List<Order> orders) {
        if (orders == null) {
            return 0;
        }
        int total = 0;
        for (Order order : orders) {
            if (order == null || order.getStatus() != OrderStatus.COMPLETED || order.getItemLines() == null) {
                continue;
            }
            for (ItemLine itemLine : order.getItemLines()) {
                if (itemLine == null || itemLine.getLibraryItem() != this) {
                    continue;
                }
                if (this.getType() == DigitalItemType.AUDIO && this.getOption() == DigitalItemOption.DOWNLOADABLE) {
                    total += itemLine.getQuantity() == null ? 0 : itemLine.getQuantity();
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

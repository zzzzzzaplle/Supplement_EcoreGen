import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
    }

    public int calculateTotalDownloads(List<Order> orders) {
        int totalDownloads = 0;

        for (Order order : orders) {
            if (order.getStatus() != OrderStatus.COMPLETED) {
                continue;
            }

            List<ItemLine> itemLines = order.getItemLines();
            if (itemLines == null) {
                continue;
            }

            for (ItemLine itemLine : itemLines) {
                LibraryItem libraryItem = itemLine.getLibraryItem();
                if (libraryItem == this) {
                    if (libraryItem instanceof DigitalItem) {
                        DigitalItem digitalItem = (DigitalItem) libraryItem;
                        if (digitalItem.getOption() == DigitalItemOption.DOWNLOADABLE) {
                            int quantity = itemLine.getQuantity() != null ? itemLine.getQuantity() : 0;
                            totalDownloads += quantity;
                        }
                    }
                }
            }
        }

        return totalDownloads;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DigitalItem that = (DigitalItem) obj;
        return true;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
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

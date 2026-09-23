import java.util.ArrayList;
import java.util.List;

enum OrderStatus {
    PENDING,
    COMPLETED
}

enum LibraryItemStatus {
    AVAILABLE,
    HOLD,
    LOAN
}

enum BookItemType {
    EBOOK,
    PRINT_FORMAT
}

enum DigitalItemOption {
    DOWNLOADABLE,
    DISC
}

enum DigitalItemType {
    AUDIO,
    VIDEO
}

abstract class LibraryItem {
    private LibraryItemStatus status;
    
    public LibraryItem() {
        this.status = LibraryItemStatus.AVAILABLE;
    }
    
    public LibraryItemStatus getStatus() {
        return status;
    }
    
    public void setStatus(LibraryItemStatus status) {
        this.status = status;
    }
}

class BookItem extends LibraryItem {
    private BookItemType type;
    
    public BookItem() {
        super();
    }
    
    public BookItemType getType() {
        return type;
    }
    
    public void setType(BookItemType type) {
        this.type = type;
    }
    
    public boolean isPrintFormat() {
        return type == BookItemType.PRINT_FORMAT;
    }
}

class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;
    
    public DigitalItem() {
        super();
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
    
    public boolean isDisc() {
        return option == DigitalItemOption.DISC;
    }
    
    public boolean isAudio() {
        return type == DigitalItemType.AUDIO;
    }
    
    public Integer calculateTotalDownloads(List<Order> orders) {
        if (orders == null) {
            return 0;
        }
        int total = 0;
        for (Order order : orders) {
            if (order == null || order.getStatus() != OrderStatus.COMPLETED) {
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
                if (line.getLibraryItem() == this
                        && this.isAudio()
                        && this.option == DigitalItemOption.DOWNLOADABLE) {
                    Integer q = line.getQuantity();
                    if (q != null) {
                        total += q;
                    }
                }
            }
        }
        return total;
    }
}

class ItemLine {
    private Integer quantity;
    private LibraryItem libraryItem;
    
    public ItemLine() {
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public LibraryItem getLibraryItem() {
        return libraryItem;
    }
    
    public void setLibraryItem(LibraryItem libraryItem) {
        this.libraryItem = libraryItem;
    }
}

class Order {
    private OrderStatus status;
    private List<ItemLine> itemLines;
    
    public Order() {
        this.status = OrderStatus.PENDING;
        this.itemLines = new ArrayList<>();
    }
    
    public boolean addItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING) {
            return false;
        }
        if (itemLine == null) {
            return false;
        }
        LibraryItem item = itemLine.getLibraryItem();
        if (item == null || item.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }
        for (ItemLine existing : itemLines) {
            if (existing != null && existing.getLibraryItem() == item) {
                return false;
            }
        }
        itemLines.add(itemLine);
        boolean isPrintFormatBook = (item instanceof BookItem) && ((BookItem) item).isPrintFormat();
        boolean isDiscDigital = (item instanceof DigitalItem) && ((DigitalItem) item).isDisc();
        if (isPrintFormatBook || isDiscDigital) {
            item.setStatus(LibraryItemStatus.HOLD);
        }
        return true;
    }
    
    public Integer countPrintBookItemIfCompleted() {
        if (status != OrderStatus.COMPLETED) {
            return 0;
        }
        int count = 0;
        for (ItemLine line : itemLines) {
            if (line == null) {
                continue;
            }
            LibraryItem item = line.getLibraryItem();
            if (item instanceof BookItem && ((BookItem) item).isPrintFormat()) {
                count++;
            }
        }
        return count;
    }
    
    public Integer removeItemLine(ItemLine itemLine) {
        if (status == OrderStatus.COMPLETED) {
            return -1;
        }
        if (itemLine == null) {
            return itemLines.size();
        }
        boolean removed = itemLines.remove(itemLine);
        if (removed) {
            LibraryItem item = itemLine.getLibraryItem();
            if (item != null && item.getStatus() == LibraryItemStatus.HOLD) {
                boolean isPrintFormatBook = (item instanceof BookItem) && ((BookItem) item).isPrintFormat();
                boolean isDiscDigital = (item instanceof DigitalItem) && ((DigitalItem) item).isDisc();
                if (isPrintFormatBook || isDiscDigital) {
                    item.setStatus(LibraryItemStatus.AVAILABLE);
                }
            }
        }
        return itemLines.size();
    }
    
    public void handleOrder() {
        setStatus(OrderStatus.COMPLETED);
        for (ItemLine line : itemLines) {
            if (line == null) {
                continue;
            }
            LibraryItem item = line.getLibraryItem();
            if (item != null && item.getStatus() == LibraryItemStatus.HOLD) {
                boolean isPrintFormatBook = (item instanceof BookItem) && ((BookItem) item).isPrintFormat();
                boolean isDiscDigital = (item instanceof DigitalItem) && ((DigitalItem) item).isDisc();
                if (isPrintFormatBook || isDiscDigital) {
                    item.setStatus(LibraryItemStatus.LOAN);
                }
            }
        }
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public List<ItemLine> getItemLines() {
        return itemLines;
    }
    
    public void setItemLines(List<ItemLine> itemLines) {
        this.itemLines = itemLines;
    }
}
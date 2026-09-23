import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    }

    public BookItemType getType() {
        return type;
    }

    public void setType(BookItemType type) {
        this.type = type;
    }
}

class DigitalItem extends LibraryItem {
    private DigitalItemOption option;
    private DigitalItemType type;

    public DigitalItem() {
    }

    public Integer calculateTotalDownloads(List<Order> orders) {
        if (orders == null) {
            return 0;
        }

        int total = 0;
        for (Order order : orders) {
            if (order == null || order.getStatus() != OrderStatus.COMPLETED || order.getItemLines() == null) {
                continue;
            }
            for (ItemLine itemLine : order.getItemLines()) {
                if (itemLine == null || itemLine.getLibraryItem() == null) {
                    continue;
                }
                LibraryItem libraryItem = itemLine.getLibraryItem();
                if (libraryItem instanceof DigitalItem) {
                    DigitalItem digitalItem = (DigitalItem) libraryItem;
                    if (digitalItem.getType() == DigitalItemType.AUDIO
                            && digitalItem.getOption() == DigitalItemOption.DOWNLOADABLE
                            && Objects.equals(digitalItem, this)) {
                        Integer quantity = itemLine.getQuantity();
                        total += quantity == null ? 0 : quantity;
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
        this.itemLines = new ArrayList<>();
    }

    public boolean addItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING || itemLine == null || itemLine.getLibraryItem() == null) {
            return false;
        }

        LibraryItem libraryItem = itemLine.getLibraryItem();
        if (libraryItem.getStatus() != LibraryItemStatus.AVAILABLE) {
            return false;
        }

        if (itemLines == null) {
            itemLines = new ArrayList<>();
        }

        for (ItemLine existing : itemLines) {
            if (existing != null && existing.getLibraryItem() == libraryItem) {
                return false;
            }
        }

        itemLines.add(itemLine);

        boolean shouldHold = false;
        if (libraryItem instanceof BookItem) {
            BookItem bookItem = (BookItem) libraryItem;
            shouldHold = bookItem.getType() == BookItemType.PRINT_FORMAT;
        } else if (libraryItem instanceof DigitalItem) {
            DigitalItem digitalItem = (DigitalItem) libraryItem;
            shouldHold = digitalItem.getOption() == DigitalItemOption.DISC;
        }

        if (shouldHold) {
            libraryItem.setStatus(LibraryItemStatus.HOLD);
        }

        return true;
    }

    public Integer countPrintBookItemIfCompleted() {
        if (status != OrderStatus.COMPLETED || itemLines == null) {
            return 0;
        }

        int count = 0;
        for (ItemLine itemLine : itemLines) {
            if (itemLine == null || itemLine.getLibraryItem() == null) {
                continue;
            }
            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem instanceof BookItem) {
                BookItem bookItem = (BookItem) libraryItem;
                if (bookItem.getType() == BookItemType.PRINT_FORMAT) {
                    Integer quantity = itemLine.getQuantity();
                    count += quantity == null ? 0 : quantity;
                }
            }
        }
        return count;
    }

    public Integer removeItemLine(ItemLine itemLine) {
        if (status != OrderStatus.PENDING) {
            return -1;
        }
        if (itemLines == null || itemLine == null || itemLine.getLibraryItem() == null) {
            return itemLines == null ? 0 : itemLines.size();
        }

        LibraryItem targetItem = itemLine.getLibraryItem();
        ItemLine found = null;
        for (ItemLine existing : itemLines) {
            if (existing != null && existing.getLibraryItem() == targetItem) {
                found = existing;
                break;
            }
        }

        if (found == null) {
            return itemLines.size();
        }

        LibraryItem libraryItem = found.getLibraryItem();
        boolean wasHold = libraryItem != null && libraryItem.getStatus() == LibraryItemStatus.HOLD;

        itemLines.remove(found);

        if (wasHold) {
            boolean shouldReset = false;
            if (libraryItem instanceof BookItem) {
                BookItem bookItem = (BookItem) libraryItem;
                shouldReset = bookItem.getType() == BookItemType.PRINT_FORMAT;
            } else if (libraryItem instanceof DigitalItem) {
                DigitalItem digitalItem = (DigitalItem) libraryItem;
                shouldReset = digitalItem.getOption() == DigitalItemOption.DISC;
            }
            if (shouldReset) {
                libraryItem.setStatus(LibraryItemStatus.AVAILABLE);
            }
        }

        return itemLines.size();
    }

    public void handleOrder() {
        this.status = OrderStatus.COMPLETED;
        if (itemLines == null) {
            return;
        }

        for (ItemLine itemLine : itemLines) {
            if (itemLine == null || itemLine.getLibraryItem() == null) {
                continue;
            }

            LibraryItem libraryItem = itemLine.getLibraryItem();
            if (libraryItem.getStatus() == LibraryItemStatus.HOLD) {
                boolean shouldLoan = false;
                if (libraryItem instanceof BookItem) {
                    BookItem bookItem = (BookItem) libraryItem;
                    shouldLoan = bookItem.getType() == BookItemType.PRINT_FORMAT;
                } else if (libraryItem instanceof DigitalItem) {
                    DigitalItem digitalItem = (DigitalItem) libraryItem;
                    shouldLoan = digitalItem.getOption() == DigitalItemOption.DISC;
                }

                if (shouldLoan) {
                    libraryItem.setStatus(LibraryItemStatus.LOAN);
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
// ==version1==
```
enum OrderStatus {
    PENDING
    COMPLETED
}

class Order {
    - OrderStatus status
    - List<ItemLine> itemLines

    //key operations
    + boolean addItemLine(itemLine)
    + integer countPrintBookItemIfCompleted()
    + integer removeItemLine(itemLine)
    + void handleOrder()
    
    //getter,setter
    + OrderStatus getStatus()
    + void setStatus(OrderStatus status)
    + List<ItemLine> getItemLines()
    + void setItemLines(List<ItemLine> itemLines)
}

class ItemLine {
    - Integer quantity
    - LibraryItem libraryItem

    //getter,setter
    + Integer getQuantity()
    + void setQuantity(Integer quantity)
    + LibraryItem getLibraryItem()
    + void setLibraryItem(LibraryItem libraryItem)
}

Order *-- "*" ItemLine : itemLines
ItemLine --> "1" LibraryItem : libraryItem

enum LibraryItemStatus {
    AVAILABLE
    HOLD
    LOAN
}

abstract class LibraryItem {
    - LibraryItemStatus status

    //getter,setter
    + LibraryItemStatus getStatus()
    + void setStatus(LibraryItemStatus status)
}

enum BookItemType {
    EBOOK
    PRINT_FORMAT
}

class BookItem extends LibraryItem {
    - BookItemType type

    //getter,setter
    + BookItemType getType()
    + void setType(BookItemType type)
}

enum DigitalItemOption {
    DOWNLOADABLE
    DISC
}

enum DigitalItemType {
    AUDIO
    VIDEO
}

class DigitalItem extends LibraryItem {
    - DigitalItemOption option
    - DigitalItemType type
    
    //key operations
    + integer calculateTotalDownloads(List<Order> orders)

    //getter,setter
    + DigitalItemOption getOption()
    + void setOption(DigitalItemOption option)
    + DigitalItemType getType()
    + void setType(DigitalItemType type)
}

```
// ==end==

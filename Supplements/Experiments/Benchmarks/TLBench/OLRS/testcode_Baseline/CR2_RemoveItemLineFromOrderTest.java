import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class CR2_RemoveItemLineFromOrderTest {

    // Test Case 1: "Remove item line (Print book) from Pending order"
    @Test
    public void testRemoveItemLinePrintBookFromPendingOrder() {
        // Setup
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        BookItem book1 = new BookItem();
        book1.setType(BookItemType.PRINT_FORMAT);
        book1.setStatus(LibraryItemStatus.HOLD);

        DigitalItem digital2 = new DigitalItem();
        digital2.setType(DigitalItemType.AUDIO);
        digital2.setOption(DigitalItemOption.DOWNLOADABLE);
        digital2.setStatus(LibraryItemStatus.AVAILABLE);

        BookItem book3 = new BookItem();
        book3.setType(BookItemType.PRINT_FORMAT);
        book3.setStatus(LibraryItemStatus.HOLD);

        ItemLine itemLine1 = new ItemLine();
        itemLine1.setLibraryItem(book1);
        itemLine1.setQuantity(1);

        ItemLine itemLine2 = new ItemLine();
        itemLine2.setLibraryItem(digital2);
        itemLine2.setQuantity(1);

        ItemLine itemLine3 = new ItemLine();
        itemLine3.setLibraryItem(book3);
        itemLine3.setQuantity(1);

        order.setItemLines(List.of(itemLine1, itemLine2, itemLine3));

        // Test removal of itemLine1
        int itemCountAfterRemoval = order.removeItemLine(itemLine1);

        // Assertions
        assertEquals("Item count mismatch after removal", 2, itemCountAfterRemoval);
        assertEquals("Library Item B1 status mismatch", LibraryItemStatus.AVAILABLE, book1.getStatus());
    }

    // Test Case 2: "Remove item line (Downloadable Audio) from Pending order"
    @Test
    public void testRemoveItemLineDownloadableAudioFromPendingOrder() {
        // Setup
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        DigitalItem digital1 = new DigitalItem();
        digital1.setType(DigitalItemType.AUDIO);
        digital1.setOption(DigitalItemOption.DOWNLOADABLE);
        digital1.setStatus(LibraryItemStatus.AVAILABLE);

        BookItem book3 = new BookItem();
        book3.setType(BookItemType.PRINT_FORMAT);
        book3.setStatus(LibraryItemStatus.HOLD);

        ItemLine itemLine1 = new ItemLine();
        itemLine1.setLibraryItem(digital1);
        itemLine1.setQuantity(1);

        ItemLine itemLine3 = new ItemLine();
        itemLine3.setLibraryItem(book3);
        itemLine3.setQuantity(1);

        order.setItemLines(List.of(itemLine1, itemLine3));

        // Test removal of itemLine1
        int itemCountAfterRemoval = order.removeItemLine(itemLine1);

        // Assertions
        assertEquals("Item count mismatch after removal", 1, itemCountAfterRemoval);
        assertEquals("Library Item D1 status mismatch", LibraryItemStatus.AVAILABLE, digital1.getStatus());
    }

    // Test Case 3: "Fail to remove from Completed order"
    @Test
    public void testFailToRemoveFromCompletedOrder() {
        // Setup
        Order order = new Order();
        order.setStatus(OrderStatus.COMPLETED);

        BookItem book1 = new BookItem();
        book1.setType(BookItemType.PRINT_FORMAT);
        book1.setStatus(LibraryItemStatus.HOLD);

        ItemLine itemLine = new ItemLine();
        itemLine.setLibraryItem(book1);
        itemLine.setQuantity(1);

        order.setItemLines(List.of(itemLine));

        // Test removal of itemLine
        int result = order.removeItemLine(itemLine);

        // Assertions
        assertEquals("Failure not indicated for removal from completed order", -1, result);
    }

    // Test Case 4: "Remove last item line from order"
    @Test
    public void testRemoveLastItemLineFromOrder() {
        // Setup
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        BookItem book2 = new BookItem();
        book2.setType(BookItemType.PRINT_FORMAT);
        book2.setStatus(LibraryItemStatus.HOLD);

        ItemLine itemLine2 = new ItemLine();
        itemLine2.setLibraryItem(book2);
        itemLine2.setQuantity(1);

        order.setItemLines(List.of(itemLine2));

        // Test removal of itemLine2
        int itemCountAfterRemoval = order.removeItemLine(itemLine2);

        // Assertions
        assertEquals("Item count mismatch after removal", 0, itemCountAfterRemoval);
        assertEquals("Library Item B2 status mismatch", LibraryItemStatus.AVAILABLE, book2.getStatus());
    }

    // Test Case 5: "Status reverts to Available for Print book"
    @Test
    public void testStatusRevertsToAvailableForPrintBook() {
        // Setup
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        BookItem book3 = new BookItem();
        book3.setType(BookItemType.PRINT_FORMAT);
        book3.setStatus(LibraryItemStatus.HOLD);

        ItemLine itemLine3 = new ItemLine();
        itemLine3.setLibraryItem(book3);
        itemLine3.setQuantity(1);

        order.setItemLines(List.of(itemLine3));

        // Test removal of itemLine3
        int itemCountAfterRemoval = order.removeItemLine(itemLine3);

        // Assertions
        assertEquals("Item count mismatch after removal", 0, itemCountAfterRemoval);
        assertEquals("Library Item B3 status mismatch", LibraryItemStatus.AVAILABLE, book3.getStatus());
    }
}


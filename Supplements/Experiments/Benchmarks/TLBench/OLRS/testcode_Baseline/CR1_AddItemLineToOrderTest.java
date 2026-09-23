import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

public class CR1_AddItemLineToOrderTest {

    @Test
    public void tc1_AddPrintBookToPendingOrder() {
        // Test Case 1 Setup
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        BookItem bookItem = new BookItem();
        bookItem.setType(BookItemType.PRINT_FORMAT);
        bookItem.setStatus(LibraryItemStatus.AVAILABLE);

        ItemLine itemLine = new ItemLine();
        itemLine.setLibraryItem(bookItem);
        itemLine.setQuantity(1);

        // Execution
        boolean result = order.addItemLine(itemLine);

        // Assertions
        assertTrue("Failed to add item line when expected success", result);
        assertEquals("Library Item status mismatch after addition", LibraryItemStatus.HOLD, bookItem.getStatus());
    }

    @Test
    public void tc2_AddDownloadableAudioToPendingOrder() {
        // Test Case 2 Setup
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        DigitalItem digitalItem = new DigitalItem();
        digitalItem.setType(DigitalItemType.AUDIO);
        digitalItem.setOption(DigitalItemOption.DOWNLOADABLE);
        digitalItem.setStatus(LibraryItemStatus.AVAILABLE);

        ItemLine itemLine = new ItemLine();
        itemLine.setLibraryItem(digitalItem);
        itemLine.setQuantity(1);

        // Execution
        boolean result = order.addItemLine(itemLine);

        // Assertions
        assertTrue("Failed to add item line when expected success", result);
        assertEquals("Library Item status mismatch after addition", LibraryItemStatus.AVAILABLE,
                digitalItem.getStatus());
    }

    @Test
    public void tc3_FailToAddDuplicateItemLine() {
        // Test Case 3 Setup
        BookItem bookItem = new BookItem();
        bookItem.setType(BookItemType.PRINT_FORMAT);
        bookItem.setStatus(LibraryItemStatus.AVAILABLE);

        ItemLine itemLine = new ItemLine();
        itemLine.setLibraryItem(bookItem);
        itemLine.setQuantity(1);

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        // First add succeeds: B1 is added to order, status changes to HOLD
        order.addItemLine(itemLine);

        // Execution: try to add same item line again (duplicate)
        boolean result = order.addItemLine(itemLine);

        // Assertions
        assertFalse("Unexpected success in adding duplicate item line", result);
    }

    @Test
    public void tc4_FailToAddToCompletedOrder() {
        // Test Case 4 Setup
        Order order = new Order();
        order.setStatus(OrderStatus.COMPLETED);

        BookItem bookItem = new BookItem();
        bookItem.setType(BookItemType.PRINT_FORMAT);
        bookItem.setStatus(LibraryItemStatus.AVAILABLE);

        ItemLine itemLine = new ItemLine();
        itemLine.setLibraryItem(bookItem);
        itemLine.setQuantity(1);

        // Execution
        boolean result = order.addItemLine(itemLine);

        // Assertions
        assertFalse("Unexpected success in adding item line to completed order", result);
    }

    @Test
    public void tc5_FailToAddLoanItem() {
        // Test Case 5 Setup
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        BookItem bookItem = new BookItem();
        bookItem.setType(BookItemType.PRINT_FORMAT);
        bookItem.setStatus(LibraryItemStatus.LOAN);

        ItemLine itemLine = new ItemLine();
        itemLine.setLibraryItem(bookItem);
        itemLine.setQuantity(1);

        // Execution
        boolean result = order.addItemLine(itemLine);

        // Assertions
        assertFalse("Unexpected success in adding item line for loaned item", result);
    }
}


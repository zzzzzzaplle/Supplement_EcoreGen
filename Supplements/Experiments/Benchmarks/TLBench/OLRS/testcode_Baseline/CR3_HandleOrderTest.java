import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

public class CR3_HandleOrderTest {

    @Test
    public void testUpdatePrintBookOrderToCompleted() {
        // Setup: Order O1 (PENDING) with B1 (PRINT_FORMAT, status: HOLD)
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        BookItem bookItem = new BookItem();
        bookItem.setType(BookItemType.PRINT_FORMAT);
        bookItem.setStatus(LibraryItemStatus.HOLD);

        ItemLine itemLine = new ItemLine();
        itemLine.setLibraryItem(bookItem);
        itemLine.setQuantity(1);

        order.setItemLines(Collections.singletonList(itemLine));

        // Testing: handleOrder()
        order.handleOrder();

        // Assertions
        assertEquals("Order status mismatch", OrderStatus.COMPLETED, order.getStatus());
        assertEquals("BookItem status mismatch", LibraryItemStatus.LOAN, bookItem.getStatus());
    }

    @Test
    public void testUpdateDiscVideoOrderToCompleted() {
        // Setup: Order O2 (PENDING) with V1 (VIDEO, DISC, status: HOLD)
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        DigitalItem digitalItem = new DigitalItem();
        digitalItem.setType(DigitalItemType.VIDEO);
        digitalItem.setOption(DigitalItemOption.DISC);
        digitalItem.setStatus(LibraryItemStatus.HOLD);

        ItemLine itemLine = new ItemLine();
        itemLine.setLibraryItem(digitalItem);
        itemLine.setQuantity(1);

        order.setItemLines(Collections.singletonList(itemLine));

        // Testing: handleOrder()
        order.handleOrder();

        // Assertions
        assertEquals("Order status mismatch", OrderStatus.COMPLETED, order.getStatus());
        assertEquals("DigitalItem status mismatch", LibraryItemStatus.LOAN, digitalItem.getStatus());
    }

    @Test
    public void testUpdateDownloadableAudioOrderToCompleted() {
        // Setup: Order O3 (PENDING) with A1 (AUDIO, DOWNLOADABLE, status: AVAILABLE)
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        DigitalItem digitalItem = new DigitalItem();
        digitalItem.setType(DigitalItemType.AUDIO);
        digitalItem.setOption(DigitalItemOption.DOWNLOADABLE);
        digitalItem.setStatus(LibraryItemStatus.AVAILABLE);

        ItemLine itemLine = new ItemLine();
        itemLine.setLibraryItem(digitalItem);
        itemLine.setQuantity(1);

        order.setItemLines(Collections.singletonList(itemLine));

        // Testing: handleOrder()
        order.handleOrder();

        // Assertions
        assertEquals("Order status mismatch", OrderStatus.COMPLETED, order.getStatus());
        assertEquals("DigitalItem status should remain AVAILABLE", LibraryItemStatus.AVAILABLE,
                digitalItem.getStatus());
    }

    @Test
    public void testFailToUpdateAlreadyCompletedOrder() {
        // Setup: Order O4 (COMPLETED)
        Order order = new Order();
        order.setStatus(OrderStatus.COMPLETED);

        // Testing: handleOrder()
        order.handleOrder();

        // Assertions
        assertEquals("Order status should remain COMPLETED", OrderStatus.COMPLETED, order.getStatus());
    }

    @Test
    public void testUpdateEBookOrderToCompleted() {
        // Setup: Order O5 (PENDING) with E1 (EBOOK, status: AVAILABLE)
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        BookItem bookItem = new BookItem();
        bookItem.setType(BookItemType.EBOOK);
        bookItem.setStatus(LibraryItemStatus.AVAILABLE);

        ItemLine itemLine = new ItemLine();
        itemLine.setLibraryItem(bookItem);
        itemLine.setQuantity(1);

        order.setItemLines(Collections.singletonList(itemLine));

        // Testing: handleOrder()
        order.handleOrder();

        // Assertions
        assertEquals("Order status mismatch", OrderStatus.COMPLETED, order.getStatus());
        assertEquals("EBook status should remain AVAILABLE", LibraryItemStatus.AVAILABLE, bookItem.getStatus());
    }
}


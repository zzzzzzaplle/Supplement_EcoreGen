import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;

public class CR5_CountPrintBookOrdersTest {

    // Test Case 1: "Count print books in completed order"
    // Setup: Order O1 (COMPLETED) with 3 item lines:
    //   B1 (PRINT_FORMAT), B2 (PRINT_FORMAT), D1 (AUDIO, DOWNLOADABLE)
    // Expected Output: 2
    @Test
    public void tc1_CountPrintBooksInCompletedOrder() {
        Order orderO1 = new Order();
        orderO1.setStatus(OrderStatus.COMPLETED);

        BookItem b1 = new BookItem();
        b1.setType(BookItemType.PRINT_FORMAT);
        ItemLine itemLine1 = new ItemLine();
        itemLine1.setLibraryItem(b1);
        itemLine1.setQuantity(1);

        BookItem b2 = new BookItem();
        b2.setType(BookItemType.PRINT_FORMAT);
        ItemLine itemLine2 = new ItemLine();
        itemLine2.setLibraryItem(b2);
        itemLine2.setQuantity(1);

        DigitalItem d1 = new DigitalItem();
        d1.setType(DigitalItemType.AUDIO);
        d1.setOption(DigitalItemOption.DOWNLOADABLE);
        ItemLine itemLine3 = new ItemLine();
        itemLine3.setLibraryItem(d1);
        itemLine3.setQuantity(1);

        orderO1.getItemLines().add(itemLine1);
        orderO1.getItemLines().add(itemLine2);
        orderO1.getItemLines().add(itemLine3);

        // Testing: countPrintBookItemIfCompleted
        int count = orderO1.countPrintBookItemIfCompleted();

        // Assertions
        assertEquals("Expected count of 2 for two print books in completed order", 2, count);
    }

    // Test Case 2: "Return 0 for Pending order"
    // Setup: Order O2 (PENDING) with 2 item lines:
    //   B3 (PRINT_FORMAT), B4 (PRINT_FORMAT)
    // Expected Output: 0
    @Test
    public void tc2_ReturnZeroForPendingOrder() {
        Order orderO2 = new Order();
        orderO2.setStatus(OrderStatus.PENDING);

        BookItem b3 = new BookItem();
        b3.setType(BookItemType.PRINT_FORMAT);
        ItemLine itemLine1 = new ItemLine();
        itemLine1.setLibraryItem(b3);
        itemLine1.setQuantity(1);

        BookItem b4 = new BookItem();
        b4.setType(BookItemType.PRINT_FORMAT);
        ItemLine itemLine2 = new ItemLine();
        itemLine2.setLibraryItem(b4);
        itemLine2.setQuantity(1);

        orderO2.getItemLines().add(itemLine1);
        orderO2.getItemLines().add(itemLine2);

        // Testing: countPrintBookItemIfCompleted
        int count = orderO2.countPrintBookItemIfCompleted();

        // Assertions
        assertEquals("Expected count of 0 for pending order", 0, count);
    }

    // Test Case 3: "Completed order with no print books"
    // Setup: Order O3 (COMPLETED) with 2 item lines:
    //   E1 (EBOOK), A1 (AUDIO, DOWNLOADABLE)
    // Expected Output: 0
    @Test
    public void tc3_CompletedOrderWithNoPrintBooks() {
        Order orderO3 = new Order();
        orderO3.setStatus(OrderStatus.COMPLETED);

        BookItem e1 = new BookItem();
        e1.setType(BookItemType.EBOOK);
        ItemLine itemLine1 = new ItemLine();
        itemLine1.setLibraryItem(e1);
        itemLine1.setQuantity(1);

        DigitalItem a1 = new DigitalItem();
        a1.setType(DigitalItemType.AUDIO);
        a1.setOption(DigitalItemOption.DOWNLOADABLE);
        ItemLine itemLine2 = new ItemLine();
        itemLine2.setLibraryItem(a1);
        itemLine2.setQuantity(1);

        orderO3.getItemLines().add(itemLine1);
        orderO3.getItemLines().add(itemLine2);

        // Testing: countPrintBookItemIfCompleted
        int count = orderO3.countPrintBookItemIfCompleted();

        // Assertions
        assertEquals("Expected count of 0 for completed order with no print books", 0, count);
    }

    // Test Case 4: "Completed order with single print book"
    // Setup: Order O4 (COMPLETED) with 1 item line:
    //   B5 (PRINT_FORMAT)
    // Expected Output: 1
    @Test
    public void tc4_CompletedOrderWithSinglePrintBook() {
        Order orderO4 = new Order();
        orderO4.setStatus(OrderStatus.COMPLETED);

        BookItem b5 = new BookItem();
        b5.setType(BookItemType.PRINT_FORMAT);
        ItemLine itemLine1 = new ItemLine();
        itemLine1.setLibraryItem(b5);
        itemLine1.setQuantity(1);

        orderO4.getItemLines().add(itemLine1);

        // Testing: countPrintBookItemIfCompleted
        int count = orderO4.countPrintBookItemIfCompleted();

        // Assertions
        assertEquals("Expected count of 1 for completed order with single print book", 1, count);
    }

    // Test Case 5: "Completed order with no items"
    // Setup: Order O5 (COMPLETED) with 0 item lines
    // Expected Output: 0
    @Test
    public void tc5_CompletedOrderWithNoItems() {
        Order orderO5 = new Order();
        orderO5.setStatus(OrderStatus.COMPLETED);

        // Testing: countPrintBookItemIfCompleted
        int count = orderO5.countPrintBookItemIfCompleted();

        // Assertions
        assertEquals("Expected count of 0 for completed order with no items", 0, count);
    }
}

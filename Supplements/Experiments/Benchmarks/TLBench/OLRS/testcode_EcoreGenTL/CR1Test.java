import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.olrs.BookItem;
import edu.olrs.BookItemType;
import edu.olrs.DigitalItem;
import edu.olrs.DigitalItemOption;
import edu.olrs.DigitalItemType;
import edu.olrs.ItemLine;
import edu.olrs.LibraryItem;
import edu.olrs.LibraryItemStatus;
import edu.olrs.OlrsFactory;
import edu.olrs.Order;
import edu.olrs.OrderStatus;

public class CR1Test {

    private OlrsFactory factory;

    @Before
    public void setUp() {
        factory = OlrsFactory.eINSTANCE;
    }

    @Test
    public void testCase1_AddPrintBookToPendingOrder() {
        Order order = createOrder(OrderStatus.PENDING);
        BookItem b1 = createBookItem(BookItemType.PRINT_FORMAT, LibraryItemStatus.AVAILABLE);

        boolean result = order.addItemLine(createItemLine(b1));

        assertTrue("Pending order should accept an available print-format book item line", result);
        assertEquals("The order should contain the new item line", 1, order.getItemLines().size());
        assertEquals("Available print-format book should become hold", LibraryItemStatus.HOLD, b1.getStatus());
    }

    @Test
    public void testCase2_AddDownloadableAudioToPendingOrder() {
        Order order = createOrder(OrderStatus.PENDING);
        DigitalItem d1 = createDigitalItem(DigitalItemType.AUDIO, DigitalItemOption.DOWNLOADABLE,
                LibraryItemStatus.AVAILABLE);

        boolean result = order.addItemLine(createItemLine(d1));

        assertTrue("Pending order should accept an available downloadable audio item line", result);
        assertEquals("The order should contain the new item line", 1, order.getItemLines().size());
        assertEquals("Downloadable audio should remain available", LibraryItemStatus.AVAILABLE, d1.getStatus());
    }

    @Test
    public void testCase3_FailToAddDuplicateItemLine() {
        Order order = createOrder(OrderStatus.PENDING);
        BookItem b1 = createBookItem(BookItemType.PRINT_FORMAT, LibraryItemStatus.AVAILABLE);
        order.addItemLine(createItemLine(b1)); // Successfully add B1 first (status becomes HOLD)

        boolean result = order.addItemLine(createItemLine(b1)); // Try to add same B1 again

        assertFalse("Order should reject another item line for the same library item", result);
        assertEquals("Duplicate item line should not be added", 1, order.getItemLines().size());
        assertEquals("Rejected duplicate should not change the existing item status", LibraryItemStatus.HOLD,
                b1.getStatus());
    }

    @Test
    public void testCase4_FailToAddToCompletedOrder() {
        Order order = createOrder(OrderStatus.COMPLETED);
        BookItem b2 = createBookItem(BookItemType.PRINT_FORMAT, LibraryItemStatus.AVAILABLE);

        boolean result = order.addItemLine(createItemLine(b2));

        assertFalse("Completed order should reject a new item line", result);
        assertEquals("Completed order should not gain item lines", 0, order.getItemLines().size());
        assertEquals("Rejected print-format book should remain available", LibraryItemStatus.AVAILABLE,
                b2.getStatus());
    }

    @Test
    public void testCase5_FailToAddLoanItem() {
        Order order = createOrder(OrderStatus.PENDING);
        BookItem b3 = createBookItem(BookItemType.PRINT_FORMAT, LibraryItemStatus.LOAN);

        boolean result = order.addItemLine(createItemLine(b3));

        assertFalse("Pending order should reject a loan item", result);
        assertEquals("Loan item should not be added", 0, order.getItemLines().size());
        assertEquals("Rejected loan item should remain loan", LibraryItemStatus.LOAN, b3.getStatus());
    }

    private Order createOrder(OrderStatus status) {
        Order order = factory.createOrder();
        order.setStatus(status);
        return order;
    }

    private BookItem createBookItem(BookItemType type, LibraryItemStatus status) {
        BookItem bookItem = factory.createBookItem();
        bookItem.setType(type);
        bookItem.setStatus(status);
        return bookItem;
    }

    private DigitalItem createDigitalItem(DigitalItemType type, DigitalItemOption option, LibraryItemStatus status) {
        DigitalItem digitalItem = factory.createDigitalItem();
        digitalItem.setType(type);
        digitalItem.setOption(option);
        digitalItem.setStatus(status);
        return digitalItem;
    }

    private ItemLine createItemLine(LibraryItem libraryItem) {
        ItemLine itemLine = factory.createItemLine();
        itemLine.setLibraryItem(libraryItem);
        itemLine.setQuantity(1);
        return itemLine;
    }
}

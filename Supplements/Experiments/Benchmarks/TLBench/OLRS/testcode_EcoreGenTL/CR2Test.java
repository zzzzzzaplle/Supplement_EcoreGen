import static org.junit.Assert.assertEquals;

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

public class CR2Test {

    private OlrsFactory factory;

    @Before
    public void setUp() {
        factory = OlrsFactory.eINSTANCE;
    }

    @Test
    public void testCase1_RemovePrintBookFromPendingOrder() {
        Order order = createOrder(OrderStatus.PENDING);
        BookItem b1 = createBookItem(BookItemType.PRINT_FORMAT, LibraryItemStatus.HOLD);
        DigitalItem d2 = createDigitalItem(DigitalItemType.AUDIO, DigitalItemOption.DOWNLOADABLE,
                LibraryItemStatus.AVAILABLE);
        BookItem b3 = createBookItem(BookItemType.PRINT_FORMAT, LibraryItemStatus.HOLD);
        ItemLine lineToRemove = addLine(order, b1);
        addLine(order, d2);
        addLine(order, b3);

        int result = order.removeItemLine(lineToRemove);

        assertEquals("Removing one of three lines should leave two item lines", 2, result);
        assertEquals("Order should contain two item lines after removal", 2, order.getItemLines().size());
        assertEquals("Removed held print book should become available", LibraryItemStatus.AVAILABLE, b1.getStatus());
    }

    @Test
    public void testCase2_RemoveDownloadableAudioFromPendingOrder() {
        Order order = createOrder(OrderStatus.PENDING);
        DigitalItem d1 = createDigitalItem(DigitalItemType.AUDIO, DigitalItemOption.DOWNLOADABLE,
                LibraryItemStatus.AVAILABLE);
        BookItem b3 = createBookItem(BookItemType.PRINT_FORMAT, LibraryItemStatus.HOLD);
        ItemLine lineToRemove = addLine(order, d1);
        addLine(order, b3);

        int result = order.removeItemLine(lineToRemove);

        assertEquals("Removing one of two lines should leave one item line", 1, result);
        assertEquals("Downloadable audio should remain available", LibraryItemStatus.AVAILABLE, d1.getStatus());
    }

    @Test
    public void testCase3_FailToRemoveFromCompletedOrder() {
        Order order = createOrder(OrderStatus.COMPLETED);
        BookItem book = createBookItem(BookItemType.PRINT_FORMAT, LibraryItemStatus.HOLD);
        ItemLine line = addLine(order, book);

        int result = order.removeItemLine(line);

        assertEquals("Completed order should reject item line removal", -1, result);
        assertEquals("Completed order should keep its item line", 1, order.getItemLines().size());
        assertEquals("Rejected removal should not change item status", LibraryItemStatus.HOLD, book.getStatus());
    }

    @Test
    public void testCase4_RemoveLastItemLineFromOrder() {
        Order order = createOrder(OrderStatus.PENDING);
        BookItem book = createBookItem(BookItemType.PRINT_FORMAT, LibraryItemStatus.HOLD);
        ItemLine line = addLine(order, book);

        int result = order.removeItemLine(line);

        assertEquals("Removing the only item line should leave zero item lines", 0, result);
        assertEquals("Order should be empty after removing the last item line", 0, order.getItemLines().size());
        assertEquals("Removed held print book should become available", LibraryItemStatus.AVAILABLE, book.getStatus());
    }

    @Test
    public void testCase5_StatusRevertsToAvailableForPrintBook() {
        Order order = createOrder(OrderStatus.PENDING);
        BookItem book = createBookItem(BookItemType.PRINT_FORMAT, LibraryItemStatus.HOLD);
        ItemLine line = addLine(order, book);

        int result = order.removeItemLine(line);

        assertEquals("Removing the print book line should leave zero item lines", 0, result);
        assertEquals("Held print book should revert to available", LibraryItemStatus.AVAILABLE, book.getStatus());
    }

    private Order createOrder(OrderStatus status) {
        Order order = factory.createOrder();
        order.setStatus(status);
        return order;
    }

    private BookItem createBookItem(BookItemType type, LibraryItemStatus status) {
        BookItem book = factory.createBookItem();
        book.setType(type);
        book.setStatus(status);
        return book;
    }

    private DigitalItem createDigitalItem(DigitalItemType type, DigitalItemOption option, LibraryItemStatus status) {
        DigitalItem digitalItem = factory.createDigitalItem();
        digitalItem.setType(type);
        digitalItem.setOption(option);
        digitalItem.setStatus(status);
        return digitalItem;
    }

    private ItemLine addLine(Order order, LibraryItem libraryItem) {
        ItemLine itemLine = factory.createItemLine();
        itemLine.setLibraryItem(libraryItem);
        itemLine.setQuantity(1);
        order.getItemLines().add(itemLine);
        return itemLine;
    }
}

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

public class CR5Test {

    private OlrsFactory factory;

    @Before
    public void setUp() {
        factory = OlrsFactory.eINSTANCE;
    }

    @Test
    public void testCase1_CountPrintBooksInCompletedOrder() {
        Order o1 = createOrder(OrderStatus.COMPLETED);
        addLine(o1, createBookItem(BookItemType.PRINT_FORMAT));
        addLine(o1, createBookItem(BookItemType.PRINT_FORMAT));
        addLine(o1, createDigitalItem(DigitalItemType.AUDIO, DigitalItemOption.DOWNLOADABLE));

        int result = o1.countPrintBookItemIfCompleted();

        assertEquals("Completed order with two print-format book item lines should return 2", 2, result);
    }

    @Test
    public void testCase2_ReturnZeroForPendingOrder() {
        Order o2 = createOrder(OrderStatus.PENDING);
        addLine(o2, createBookItem(BookItemType.PRINT_FORMAT));
        addLine(o2, createBookItem(BookItemType.PRINT_FORMAT));

        int result = o2.countPrintBookItemIfCompleted();

        assertEquals("Pending order should return 0 even when it contains print-format books", 0, result);
    }

    @Test
    public void testCase3_CompletedOrderWithNoPrintBooks() {
        Order o3 = createOrder(OrderStatus.COMPLETED);
        addLine(o3, createBookItem(BookItemType.EBOOK));
        addLine(o3, createDigitalItem(DigitalItemType.AUDIO, DigitalItemOption.DOWNLOADABLE));

        int result = o3.countPrintBookItemIfCompleted();

        assertEquals("Completed order with no print-format books should return 0", 0, result);
    }

    @Test
    public void testCase4_CompletedOrderWithSinglePrintBook() {
        Order o4 = createOrder(OrderStatus.COMPLETED);
        addLine(o4, createBookItem(BookItemType.PRINT_FORMAT));

        int result = o4.countPrintBookItemIfCompleted();

        assertEquals("Completed order with one print-format book item line should return 1", 1, result);
    }

    @Test
    public void testCase5_CompletedOrderWithNoItems() {
        Order o5 = createOrder(OrderStatus.COMPLETED);

        int result = o5.countPrintBookItemIfCompleted();

        assertEquals("Completed order with no item lines should return 0", 0, result);
    }

    private Order createOrder(OrderStatus status) {
        Order order = factory.createOrder();
        order.setStatus(status);
        return order;
    }

    private BookItem createBookItem(BookItemType type) {
        BookItem bookItem = factory.createBookItem();
        bookItem.setType(type);
        bookItem.setStatus(LibraryItemStatus.AVAILABLE);
        return bookItem;
    }

    private DigitalItem createDigitalItem(DigitalItemType type, DigitalItemOption option) {
        DigitalItem digitalItem = factory.createDigitalItem();
        digitalItem.setType(type);
        digitalItem.setOption(option);
        digitalItem.setStatus(LibraryItemStatus.AVAILABLE);
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

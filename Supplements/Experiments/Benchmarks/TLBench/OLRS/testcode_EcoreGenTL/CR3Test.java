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

public class CR3Test {

    private OlrsFactory factory;

    @Before
    public void setUp() {
        factory = OlrsFactory.eINSTANCE;
    }

    @Test
    public void testCase1_UpdatePrintBookOrderToCompleted() {
        Order order = createOrder(OrderStatus.PENDING);
        BookItem book = createBookItem(BookItemType.PRINT_FORMAT, LibraryItemStatus.HOLD);
        addLine(order, book);

        order.handleOrder();

        assertEquals("Order should become completed", OrderStatus.COMPLETED, order.getStatus());
        assertEquals("Held print book should become loan after completion", LibraryItemStatus.LOAN, book.getStatus());
    }

    @Test
    public void testCase2_UpdateDiscVideoOrderToCompleted() {
        Order order = createOrder(OrderStatus.PENDING);
        DigitalItem video = createDigitalItem(DigitalItemType.VIDEO, DigitalItemOption.DISC, LibraryItemStatus.HOLD);
        addLine(order, video);

        order.handleOrder();

        assertEquals("Order should become completed", OrderStatus.COMPLETED, order.getStatus());
        assertEquals("Held disc video should become loan after completion", LibraryItemStatus.LOAN, video.getStatus());
    }

    @Test
    public void testCase3_UpdateDownloadableAudioOrderToCompleted() {
        Order order = createOrder(OrderStatus.PENDING);
        DigitalItem audio = createDigitalItem(DigitalItemType.AUDIO, DigitalItemOption.DOWNLOADABLE,
                LibraryItemStatus.AVAILABLE);
        addLine(order, audio);

        order.handleOrder();

        assertEquals("Order should become completed", OrderStatus.COMPLETED, order.getStatus());
        assertEquals("Downloadable audio should remain available", LibraryItemStatus.AVAILABLE, audio.getStatus());
    }

    @Test
    public void testCase4_FailToUpdateAlreadyCompletedOrder() {
        Order order = createOrder(OrderStatus.COMPLETED);
        BookItem book = createBookItem(BookItemType.PRINT_FORMAT, LibraryItemStatus.HOLD);
        addLine(order, book);

        order.handleOrder();

        assertEquals("Already completed order should remain completed", OrderStatus.COMPLETED, order.getStatus());
        assertEquals("Already completed order should not be handled again", LibraryItemStatus.HOLD, book.getStatus());
    }

    @Test
    public void testCase5_UpdateEBookOrderToCompleted() {
        Order order = createOrder(OrderStatus.PENDING);
        BookItem ebook = createBookItem(BookItemType.EBOOK, LibraryItemStatus.AVAILABLE);
        addLine(order, ebook);

        order.handleOrder();

        assertEquals("Order should become completed", OrderStatus.COMPLETED, order.getStatus());
        assertEquals("eBook should remain available", LibraryItemStatus.AVAILABLE, ebook.getStatus());
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

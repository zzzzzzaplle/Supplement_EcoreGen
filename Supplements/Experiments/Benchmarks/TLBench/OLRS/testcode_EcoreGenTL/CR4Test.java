import static org.junit.Assert.assertEquals;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.Before;
import org.junit.Test;

import edu.olrs.DigitalItem;
import edu.olrs.DigitalItemOption;
import edu.olrs.DigitalItemType;
import edu.olrs.ItemLine;
import edu.olrs.LibraryItemStatus;
import edu.olrs.OlrsFactory;
import edu.olrs.Order;
import edu.olrs.OrderStatus;

public class CR4Test {

    private OlrsFactory factory;

    @Before
    public void setUp() {
        factory = OlrsFactory.eINSTANCE;
    }

    @Test
    public void testCase1_CountSingleDownloadForAudioItem() {
        DigitalItem audio = createDigitalItem(DigitalItemType.AUDIO, DigitalItemOption.DOWNLOADABLE);
        EList<Order> orders = new BasicEList<Order>();
        orders.add(createOrderWithItem(OrderStatus.COMPLETED, audio));

        int result = audio.calculateTotalDownloads(orders);

        assertEquals("One completed order with the downloadable audio should count once", 1, result);
    }

    @Test
    public void testCase2_IgnoreDiscAudioItems() {
        DigitalItem audio = createDigitalItem(DigitalItemType.AUDIO, DigitalItemOption.DISC);
        EList<Order> orders = new BasicEList<Order>();
        orders.add(createOrderWithItem(OrderStatus.COMPLETED, audio));

        int result = audio.calculateTotalDownloads(orders);

        assertEquals("Disc audio items should not count as downloads", 0, result);
    }

    @Test
    public void testCase3_CountMultipleDownloads() {
        DigitalItem targetAudio = createDigitalItem(DigitalItemType.AUDIO, DigitalItemOption.DOWNLOADABLE);
        DigitalItem discAudio = createDigitalItem(DigitalItemType.AUDIO, DigitalItemOption.DISC);
        EList<Order> orders = new BasicEList<Order>();
        orders.add(createOrderWithItem(OrderStatus.COMPLETED, targetAudio));
        orders.add(createOrderWithItem(OrderStatus.COMPLETED, targetAudio));
        orders.add(createOrderWithItem(OrderStatus.COMPLETED, targetAudio));
        orders.add(createOrderWithItem(OrderStatus.COMPLETED, discAudio));

        int result = targetAudio.calculateTotalDownloads(orders);

        assertEquals("Three completed orders containing the downloadable audio should count as three downloads", 3,
                result);
    }

    @Test
    public void testCase4_IgnorePendingOrders() {
        DigitalItem audio = createDigitalItem(DigitalItemType.AUDIO, DigitalItemOption.DOWNLOADABLE);
        EList<Order> orders = new BasicEList<Order>();
        orders.add(createOrderWithItem(OrderStatus.PENDING, audio));

        int result = audio.calculateTotalDownloads(orders);

        assertEquals("Pending orders should not count as downloads", 0, result);
    }

    @Test
    public void testCase5_NoDownloadsCase() {
        DigitalItem audio = createDigitalItem(DigitalItemType.AUDIO, DigitalItemOption.DOWNLOADABLE);
        EList<Order> orders = new BasicEList<Order>();

        int result = audio.calculateTotalDownloads(orders);

        assertEquals("An audio item with no completed download orders should return zero", 0, result);
    }

    private DigitalItem createDigitalItem(DigitalItemType type, DigitalItemOption option) {
        DigitalItem digitalItem = factory.createDigitalItem();
        digitalItem.setType(type);
        digitalItem.setOption(option);
        digitalItem.setStatus(LibraryItemStatus.AVAILABLE);
        return digitalItem;
    }

    private Order createOrderWithItem(OrderStatus status, DigitalItem digitalItem) {
        Order order = factory.createOrder();
        order.setStatus(status);
        ItemLine itemLine = factory.createItemLine();
        itemLine.setLibraryItem(digitalItem);
        itemLine.setQuantity(1);
        order.getItemLines().add(itemLine);
        return order;
    }
}

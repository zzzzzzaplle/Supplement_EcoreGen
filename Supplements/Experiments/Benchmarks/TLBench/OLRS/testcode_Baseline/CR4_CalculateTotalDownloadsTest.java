import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

// Other required imports

public class CR4_CalculateTotalDownloadsTest {

    @Test
    public void testCase1_CountSingleDownloadForAudioItem() {
        // Setup: Create digital audio item A1, set as downloadable
        DigitalItem audioItemA1 = new DigitalItem();
        audioItemA1.setType(DigitalItemType.AUDIO);
        audioItemA1.setOption(DigitalItemOption.DOWNLOADABLE);

        // Setup: Create order O1, set as COMPLETED, and add item line with audio item
        // A1
        Order orderO1 = new Order();
        orderO1.setStatus(OrderStatus.COMPLETED);

        ItemLine itemLine1 = new ItemLine();
        itemLine1.setLibraryItem(audioItemA1);
        itemLine1.setQuantity(1);
        orderO1.getItemLines().add(itemLine1);

        // Setup: Add order O1 to list
        List<Order> orders = new ArrayList<>();
        orders.add(orderO1);

        // Testing: calculateTotalDownloads(List<Order>): int
        int totalDownloads = audioItemA1.calculateTotalDownloads(orders);

        // Assertions
        assertEquals("Expected total download count is incorrect", 1, totalDownloads);
    }

    @Test
    public void testCase2_IgnoreDiscAudioItems() {
        // Setup: Create digital audio item A2, set as Disc
        DigitalItem audioItemA2 = new DigitalItem();
        audioItemA2.setType(DigitalItemType.AUDIO);
        audioItemA2.setOption(DigitalItemOption.DISC);

        // Setup: Create order O2, set as COMPLETED, and add item line with audio item
        // A2
        Order orderO2 = new Order();
        orderO2.setStatus(OrderStatus.COMPLETED);

        ItemLine itemLine2 = new ItemLine();
        itemLine2.setLibraryItem(audioItemA2);
        itemLine2.setQuantity(1);
        orderO2.getItemLines().add(itemLine2);

        // Setup: Add order O2 to list
        List<Order> orders = new ArrayList<>();
        orders.add(orderO2);

        // Testing: calculateTotalDownloads(List<Order>): int
        int totalDownloads = audioItemA2.calculateTotalDownloads(orders);

        // Assertions
        assertEquals("Expected total download count is incorrect", 0, totalDownloads);
    }

    @Test
    public void testCase3_CountMultipleDownloads() {
        // Setup: Create digital audio item A3, set as downloadable
        DigitalItem audioItemA3 = new DigitalItem();
        audioItemA3.setType(DigitalItemType.AUDIO);
        audioItemA3.setOption(DigitalItemOption.DOWNLOADABLE);

        // Setup: Create several COMPLETED orders with this digital item
        Order orderO1 = new Order();
        orderO1.setStatus(OrderStatus.COMPLETED);
        ItemLine itemLine1 = new ItemLine();
        itemLine1.setLibraryItem(audioItemA3);
        itemLine1.setQuantity(1);
        orderO1.getItemLines().add(itemLine1);

        Order orderO2 = new Order();
        orderO2.setStatus(OrderStatus.COMPLETED);
        ItemLine itemLine2 = new ItemLine();
        itemLine2.setLibraryItem(audioItemA3);
        itemLine2.setQuantity(1);
        orderO2.getItemLines().add(itemLine2);

        Order orderO3 = new Order();
        orderO3.setStatus(OrderStatus.COMPLETED);
        ItemLine itemLine3 = new ItemLine();
        itemLine3.setLibraryItem(audioItemA3);
        itemLine3.setQuantity(1);
        orderO3.getItemLines().add(itemLine3);

        // O4 includes a DISC item, thus it should not be counted
        DigitalItem audioItemA4 = new DigitalItem();
        audioItemA4.setType(DigitalItemType.AUDIO);
        audioItemA4.setOption(DigitalItemOption.DISC);

        Order orderO4 = new Order();
        orderO4.setStatus(OrderStatus.COMPLETED);
        ItemLine itemLine4 = new ItemLine();
        itemLine4.setLibraryItem(audioItemA4);
        itemLine4.setQuantity(1);
        orderO4.getItemLines().add(itemLine4);

        // Setup: Add orders to list
        List<Order> orders = new ArrayList<>();
        orders.add(orderO1);
        orders.add(orderO2);
        orders.add(orderO3);
        orders.add(orderO4);

        // Testing: calculateTotalDownloads(List<Order>): int
        int totalDownloads = audioItemA3.calculateTotalDownloads(orders);

        // Assertions
        assertEquals("Expected total download count is incorrect", 3, totalDownloads);
    }

    @Test
    public void testCase4_IgnorePendingOrders() {
        // Setup: Create digital audio item A4, set as downloadable
        DigitalItem audioItemA4 = new DigitalItem();
        audioItemA4.setType(DigitalItemType.AUDIO);
        audioItemA4.setOption(DigitalItemOption.DOWNLOADABLE);

        // Setup: Create order O4, set as PENDING, and add item line with audio item A4
        Order orderO4 = new Order();
        orderO4.setStatus(OrderStatus.PENDING);

        ItemLine itemLine4 = new ItemLine();
        itemLine4.setLibraryItem(audioItemA4);
        itemLine4.setQuantity(1);
        orderO4.getItemLines().add(itemLine4);

        // Setup: Add order O4 to list
        List<Order> orders = new ArrayList<>();
        orders.add(orderO4);

        // Testing: calculateTotalDownloads(List<Order>): int
        int totalDownloads = audioItemA4.calculateTotalDownloads(orders);

        // Assertions
        assertEquals("Expected total download count is incorrect", 0, totalDownloads);
    }

    @Test
    public void testCase5_NoDownloadsCase() {
        // Setup: Create a new digital audio item A5, set as downloadable
        DigitalItem audioItemA5 = new DigitalItem();
        audioItemA5.setType(DigitalItemType.AUDIO);
        audioItemA5.setOption(DigitalItemOption.DOWNLOADABLE);

        // No orders to add

        List<Order> orders = new ArrayList<>(); // Empty list

        // Testing: calculateTotalDownloads(List<Order>): int
        int totalDownloads = audioItemA5.calculateTotalDownloads(orders);

        // Assertions
        assertEquals("Expected total download count is incorrect", 0, totalDownloads);
    }
}



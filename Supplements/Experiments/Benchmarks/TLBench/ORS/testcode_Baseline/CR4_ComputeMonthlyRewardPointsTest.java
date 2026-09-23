import static org.junit.Assert.*;
import org.junit.Test;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CR4_ComputeMonthlyRewardPointsTest {

    // Helper methods
    Customer createCustomer() {
        return new Customer();
    }
    
    MembershipPackage createMembershipPackage(Award[] awards) {
        MembershipPackage membershipPackage = new MembershipPackage();
        membershipPackage.setAwards(awards);
        return membershipPackage;
    }
    
    Trip createTrip(String departureTime) {
        Trip trip = new Trip();
        trip.setDepartureTime(departureTime);
        return trip;
    }
    
    Booking createBooking(Customer customer, Trip trip, int numberOfSeats, Date bookingDate) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(bookingDate);
        return booking;
    }
    
    Date createDate(String dateStr) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.parse(dateStr);
    }

    @Test
    public void testPointsCalculationWithMultipleBookings() throws ParseException {
        Customer customer = createCustomer();
        MembershipPackage packageWithPoints = createMembershipPackage(new Award[] { Award.POINTS });
        customer.setMembershipPackage(packageWithPoints);

        Trip trip1 = createTrip("2023-12-25 12:00");

        Trip trip2 = createTrip("2023-12-26 12:00");

        Booking booking1 = createBooking(customer, trip1, 2, createDate("2023-12-01 10:00"));

        Booking booking2 = createBooking(customer, trip2, 3, createDate("2023-12-05 10:00"));

        customer.addBooking(booking1);
        customer.addBooking(booking2);

        int points = customer.computeMonthlyRewardPoints("2023-12");
        assertEquals("Total points calculation mismatch", 25, points);
    }

    @Test
    public void testZeroPointsWithExpiredBookings() throws ParseException {
        Customer customer = createCustomer();
        MembershipPackage packageWithPoints = createMembershipPackage(new Award[] { Award.POINTS });
        customer.setMembershipPackage(packageWithPoints);

        Trip trip = createTrip("2024-12-26 12:00");

        Booking booking = createBooking(customer, trip, 4, createDate("2024-12-01 10:00"));

        customer.addBooking(booking);

        int points = customer.computeMonthlyRewardPoints("2023-12");
        assertEquals("Total points calculation mismatch", 0, points);
    }

    @Test
    public void testPartialMonthInclusion() throws ParseException {
        Customer customer = createCustomer();
        MembershipPackage packageWithPoints = createMembershipPackage(new Award[] { Award.POINTS });
        customer.setMembershipPackage(packageWithPoints);

        Trip trip = createTrip("2023-12-25 12:00");

        Booking booking1 = createBooking(customer, trip, 2, createDate("2023-11-30 10:00"));

        Booking booking2 = createBooking(customer, trip, 3, createDate("2023-12-01 10:00"));

        customer.addBooking(booking1);
        customer.addBooking(booking2);

        int points = customer.computeMonthlyRewardPoints("2023-12");
        assertEquals("Total points calculation mismatch", 15, points);
    }

    @Test
    public void testMultipleSeatsEdgeCase() throws ParseException {
        Customer customer = createCustomer();
        MembershipPackage packageWithPoints = createMembershipPackage(new Award[] { Award.POINTS });
        customer.setMembershipPackage(packageWithPoints);

        Trip trip = createTrip("2024-03-25 12:00");

        Booking booking = createBooking(customer, trip, 2, createDate("2023-12-10 10:00"));

        customer.addBooking(booking);

        int points = customer.computeMonthlyRewardPoints("2023-12");
        assertEquals("Total points calculation mismatch", 10, points);
    }

    @Test
    public void testLargeSeatQuantity() throws ParseException {
        Customer customer1 = createCustomer();
        MembershipPackage packageWithPoints1 = createMembershipPackage(new Award[] { Award.POINTS });
        customer1.setMembershipPackage(packageWithPoints1);

        Customer customer2 = createCustomer();
        MembershipPackage packageWithPoints2 = createMembershipPackage(new Award[] { Award.POINTS });
        customer2.setMembershipPackage(packageWithPoints2);

        Trip trip1 = createTrip("2024-05-25 12:00");

        Trip trip2 = createTrip("2024-06-25 12:00");

        Trip trip3 = createTrip("2024-07-25 12:00");

        Booking booking1 = createBooking(customer1, trip1, 50, createDate("2024-01-10 10:00"));

        Booking booking2 = createBooking(customer1, trip2, 50, createDate("2024-01-15 10:00"));

        Booking booking3 = createBooking(customer2, trip3, 50, createDate("2024-01-10 10:00"));

        customer1.addBooking(booking1);
        customer1.addBooking(booking2);
        customer2.addBooking(booking3);

        int pointsCustomer1 = customer1.computeMonthlyRewardPoints("2024-01");
        int pointsCustomer2 = customer2.computeMonthlyRewardPoints("2024-01");

        assertEquals("Customer C8 points calculation mismatch", 500, pointsCustomer1);
        assertEquals("Customer C9 points calculation mismatch", 250, pointsCustomer2);
    }

    @Test
    public void tc6_pointsCalculationWithNullMembership() throws ParseException {
        // Test logic: "Points calculation with null membership"
        Customer customer = createCustomer();
        customer.setMembershipPackage(null);
        int points = customer.computeMonthlyRewardPoints("2023-12");
        assertEquals("Expected 0 points with null membership", 0, points);
    }

    @Test
    public void tc7_pointsCalculationWithNullCurrentMonth() throws ParseException {
        // Test logic: "Points calculation with null current month"
        Customer customer = createCustomer();
        MembershipPackage packageWithPoints = createMembershipPackage(new Award[] { Award.POINTS });
        customer.setMembershipPackage(packageWithPoints);
        
        Trip trip = createTrip("2023-12-25 12:00");
        Booking booking = createBooking(customer, trip, 2, createDate("2023-12-01 10:00"));
        customer.addBooking(booking);
        
        int points = customer.computeMonthlyRewardPoints(null);
        assertEquals("Expected 0 points with null month", 0, points);
    }
}

/*
 * compile_result:
 * 
 * 
 * 
 * run_result:
 * JUnit version 4.13.2
 * .....
 * Time: 0.046
 * 
 * OK (5 tests)
 * 
 * 
 */
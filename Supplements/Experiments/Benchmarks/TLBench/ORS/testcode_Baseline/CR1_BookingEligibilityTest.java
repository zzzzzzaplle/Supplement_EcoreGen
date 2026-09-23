import static org.junit.Assert.*;
import org.junit.Test;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CR1_BookingEligibilityTest {

    // Required instance variables
    private Driver driver;
    private Customer customer;
    private Trip trip;
    private Booking booking;

    // Helper methods
    Driver createDriver() {
        return new Driver();
    }
    
    Trip createTrip(String departureTime, String arrivalTime, int numberOfSeats) {
        Trip trip = new Trip();
        trip.setDepartureTime(departureTime);
        trip.setArrivalTime(arrivalTime);
        trip.setNumberOfSeats(numberOfSeats);
        return trip;
    }
    
    Customer createCustomer() {
        return new Customer();
    }
    
    Booking createBooking(Customer customer, Trip trip, int numberOfSeats, Date bookingDate) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(bookingDate);
        return booking;
    }
    
    Date parseDate(String dateString) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);
    }

    @Test
    public void tc1_validBookingWithAvailableSeatsAndNoOverlap() throws ParseException {
        // Test logic: "Valid booking with available seats and no overlap"
        driver = createDriver();
        trip = createTrip("2023-12-25 14:00", "2023-12-25 16:00", 5);
        driver.addTrip(trip);

        customer = createCustomer();
        booking = createBooking(customer, trip, 3, parseDate("2023-12-25 11:00"));

        boolean isEligible = booking.isBookingEligible();
        if (isEligible)
            booking.updateTripSeats();

        // Assertions
        assertTrue("Booking eligibility mismatch", isEligible);
        assertEquals("Trip seats not updated correctly", 2, trip.getNumberOfSeats());
    }

    @Test
    public void tc2_bookingDeniedDueToSeatShortage() throws ParseException {
        // Test logic: "Booking denied due to seat shortage"
        driver = createDriver();
        trip = createTrip("2023-12-25 10:00", "2023-12-25 12:00", 2);
        driver.addTrip(trip);

        customer = createCustomer();
        booking = createBooking(customer, trip, 3, parseDate("2023-12-25 07:30"));

        boolean isEligible = booking.isBookingEligible();
        if (isEligible)
            booking.updateTripSeats();

        // Assertions
        assertFalse("Booking eligibility mismatch", isEligible);
        assertEquals("Trip seats not updated correctly", 2, trip.getNumberOfSeats());
    }

    @Test
    public void tc3_bookingDeniedDueToTimeCutoff() throws ParseException {
        // Test logic: "Booking denied due to time cutoff (exactly 2 hours before)"
        trip = createTrip("2023-12-25 14:00", "2023-12-25 16:00", 50);

        customer = createCustomer();
        booking = createBooking(customer, trip, 3, parseDate("2023-12-25 12:00"));

        boolean isEligible = booking.isBookingEligible();
        if (isEligible)
            booking.updateTripSeats();
        // Assertions
        assertFalse("Booking eligibility mismatch", isEligible);
        assertEquals("Trip seats not updated correctly", 50, trip.getNumberOfSeats());
    }

    @Test
    public void tc4_bookingAllowedWithMultipleNonOverlappingTrips() throws ParseException {
        // Test logic: "Booking allowed with multiple non-overlapping trips"
        trip = createTrip("2023-12-25 08:00", "2023-12-25 10:00", 50);

        customer = createCustomer();
        booking = createBooking(customer, trip, 2, parseDate("2023-12-23 12:00"));

        customer.addBooking(booking);

        Trip trip2 = createTrip("2023-12-25 12:00", "2023-12-25 14:00", 40);

        Booking booking2 = createBooking(customer, trip2, 4, parseDate("2023-12-23 14:00"));

        boolean isEligible = booking2.isBookingEligible();
        if (isEligible)
            booking2.updateTripSeats();
        // Assertions
        assertTrue("Booking eligibility mismatch", isEligible);
        assertEquals("Trip seats not updated correctly", 36, trip2.getNumberOfSeats());
    }

    @Test
    public void tc5_bookingDeniedWhenCustomerHasOverlappingBooking() throws ParseException {
        // Test logic: "Booking denied when customer has overlapping booking"
        trip = createTrip("2023-12-25 13:00", "2023-12-25 15:00", 50);

        customer = createCustomer();
        booking = createBooking(customer, trip, 2, parseDate("2023-12-23 12:00"));
        customer.addBooking(booking);

        Trip trip2 = createTrip("2023-12-25 14:00", "2023-12-25 15:30", 40);
        Booking booking2 = createBooking(customer, trip2, 4, parseDate("2023-12-23 14:00"));

        boolean isEligible = booking2.isBookingEligible();
        if (isEligible)
            booking2.updateTripSeats();
        // Assertions
        assertFalse("Booking eligibility mismatch", isEligible);
        assertEquals("Trip seats not updated correctly", 40, trip2.getNumberOfSeats());
    }

    @Test
    public void tc6_bookingDeniedWithNullInputs() throws ParseException {
        // Test logic: "Booking denied with null inputs"
        
        // Case 1: Null Customer
        trip = createTrip("2023-12-25 14:00", "2023-12-25 16:00", 5);
        booking = createBooking(null, trip, 1, parseDate("2023-12-25 10:00"));
        assertFalse("Booking should be denied with null customer", booking.isBookingEligible());

        // Case 2: Null Trip
        customer = createCustomer();
        booking = createBooking(customer, null, 1, parseDate("2023-12-25 10:00"));
        assertFalse("Booking should be denied with null trip", booking.isBookingEligible());
    }

    @Test
    public void tc7_bookingDeniedWithNullBookingDate() throws ParseException {
        // Test logic: "Booking denied with null booking date"
        trip = createTrip("2023-12-25 14:00", "2023-12-25 16:00", 5);
        customer = createCustomer();
        booking = createBooking(customer, trip, 1, null);
        assertFalse("Booking should be denied with null booking date", booking.isBookingEligible());
    }
}

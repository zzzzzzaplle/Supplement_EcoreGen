import static org.junit.Assert.*;
import org.junit.Test;
import java.text.ParseException;

public class CR2_CalculateDiscountedTripPriceTest {

    private Trip trip;
    private Customer customer;
    private MembershipPackage membershipPackage;

    // Helper methods
    Trip createTrip(double price, String departureTime) {
        Trip trip = new Trip();
        trip.setPrice(price);
        trip.setDepartureTime(departureTime);
        return trip;
    }
    
    Customer createCustomer() {
        return new Customer();
    }
    
    MembershipPackage createMembershipPackage(Award[] awards) {
        MembershipPackage membershipPackage = new MembershipPackage();
        membershipPackage.setAwards(awards);
        return membershipPackage;
    }

    @Test
    public void tc1_discountAppliedForEarlyBooking() throws ParseException {
        // Setup for Test Case 1
        trip = createTrip(100.0, "2023-12-25 08:00");

        customer = createCustomer();
        membershipPackage = createMembershipPackage(new Award[] { Award.DISCOUNTS });
        customer.setMembershipPackage(membershipPackage);

        // Testing: calculateDiscountedPrice(Customer, String)
        double actualPrice = trip.calculateDiscountedPrice(customer, "2023-12-24 07:00");

        // Assertions
        assertEquals("Expected price after 20% discount for early booking is incorrect", 80.0, actualPrice, 0.01);
    }

    @Test
    public void tc2_discountDeniedForLateBooking() throws ParseException {
        // Setup for Test Case 2
        trip = createTrip(200.0, "2023-12-25 12:00");

        customer = createCustomer();
        membershipPackage = createMembershipPackage(new Award[] { Award.DISCOUNTS });
        customer.setMembershipPackage(membershipPackage);

        // Testing: calculateDiscountedPrice(Customer, String)
        double actualPrice = trip.calculateDiscountedPrice(customer, "2023-12-25 10:30");

        // Assertions
        assertEquals("Expected price without discount for late booking is incorrect", 200.0, actualPrice, 0.01);
    }

    @Test
    public void tc3_exact24HourBoundaryForDiscount() throws ParseException {
        // Setup for Test Case 3
        trip = createTrip(100.0, "2023-12-25 08:00");

        customer = createCustomer();
        membershipPackage = createMembershipPackage(new Award[] { Award.DISCOUNTS });
        customer.setMembershipPackage(membershipPackage);

        // Testing: calculateDiscountedPrice(Customer, String)
        double actualPrice = trip.calculateDiscountedPrice(customer, "2023-12-24 08:00");

        // Assertions
        assertEquals("Expected price at exact 24-hour boundary is incorrect", 80.0, actualPrice, 0.01);
    }

    @Test
    public void tc4_noDiscountWithoutMembership() throws ParseException {
        // Setup for Test Case 4
        trip = createTrip(200.0, "2023-12-26 12:00");

        customer = createCustomer();
        // No membership package

        // Testing: calculateDiscountedPrice(Customer, String)
        double actualPrice = trip.calculateDiscountedPrice(customer, "2023-12-24 12:00");

        // Assertions
        assertEquals("Expected price without discount due to lack of membership is incorrect", 200.0, actualPrice,
                0.01);
    }

    @Test
    public void tc5_discountAppliesOnlyToEligibleMembershipType() throws ParseException {
        // Setup for Test Case 5
        trip = createTrip(150.0, "2023-12-25 08:00");

        customer = createCustomer();
        membershipPackage = createMembershipPackage(new Award[] { Award.CASHBACK }); // No DISCOUNTS award
        customer.setMembershipPackage(membershipPackage);

        // Testing: calculateDiscountedPrice(Customer, String)
        double actualPrice = trip.calculateDiscountedPrice(customer, "2023-12-23 02:00");

        // Assertions
        assertEquals("Expected price without discount due to ineligible membership type is incorrect", 150.0,
                actualPrice, 0.01);
    }

    @Test
    public void tc6_priceCalculationWithNullMembershipPackage() throws ParseException {
        // Test logic: "Price calculation with null membership package"
        trip = createTrip(150.0, "2023-12-25 08:00");
        customer = createCustomer();
        customer.setMembershipPackage(null);
        
        double actualPrice = trip.calculateDiscountedPrice(customer, "2023-12-23 02:00");
        assertEquals("Expected original price with null membership package", 150.0, actualPrice, 0.01);
    }

    @Test
    public void tc7_priceCalculationWithNullBookingTime() throws ParseException {
        // Test logic: "Price calculation with null booking time"
        trip = createTrip(100.0, "2023-12-25 08:00");
        customer = createCustomer();
        membershipPackage = createMembershipPackage(new Award[] { Award.DISCOUNTS });
        customer.setMembershipPackage(membershipPackage);
        
        double actualPrice = trip.calculateDiscountedPrice(customer, null);
        assertEquals("Expected original price with null booking time", 100.0, actualPrice, 0.01);
    }
}

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {}

    public MembershipPackage getMembershipPackage() { return membershipPackage; }
    public void setMembershipPackage(MembershipPackage membershipPackage) { this.membershipPackage = membershipPackage; }
    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking booking) { this.bookings.add(booking); }
    
    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        if (booking.isBookingEligible()) {
            booking.updateTripSeats();
            this.addBooking(booking);
        }
    }
    
    public int computeMonthlyRewardPoints(String currentMonth) {
        int total = 0;
        for (Booking b : bookings) {
            if (b.isInMonth(currentMonth)) {
                total += b.getTrip().calculateMonthlyPoints(this, currentMonth);
            }
        }
        return total;
    }
}

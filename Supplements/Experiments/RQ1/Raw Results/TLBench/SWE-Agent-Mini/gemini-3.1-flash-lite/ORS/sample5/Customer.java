import java.util.*;

public class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {}

    public MembershipPackage getMembershipPackage() { return membershipPackage; }
    public void setMembershipPackage(MembershipPackage membershipPackage) { this.membershipPackage = membershipPackage; }
    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking booking) { bookings.add(booking); }

    public void bookTrip(Trip trip, int numberOfSeats) {
        // Implementation needed
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        return 0; // Implementation needed
    }
}

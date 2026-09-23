import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings = new ArrayList<>();

    public Customer() {
    }

    public MembershipPackage getMembershipPackage() { return membershipPackage; }
    public void setMembershipPackage(MembershipPackage membershipPackage) { this.membershipPackage = membershipPackage; }
    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking booking) { if (booking != null) bookings.add(booking); }
    public void bookTrip(Trip trip, int numberOfSeats) { }
    public int computeMonthlyRewardPoints(String currentMonth) { return 0; }
}

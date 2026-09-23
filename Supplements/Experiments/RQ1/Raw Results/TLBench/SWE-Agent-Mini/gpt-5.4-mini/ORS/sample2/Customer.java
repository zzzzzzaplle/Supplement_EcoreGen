import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings;

    public Customer() {
        bookings = new ArrayList<Booking>();
    }

    public MembershipPackage getMembershipPackage() { return membershipPackage; }
    public void setMembershipPackage(MembershipPackage membershipPackage) { this.membershipPackage = membershipPackage; }
    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking booking) { if (booking != null) bookings.add(booking); }
    public void bookTrip(Trip trip, int numberOfSeats) { }
    public int computeMonthlyRewardPoints(String currentMonth) {
        int points = 0;
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS) || bookings == null) return 0;
        for (Booking b : bookings) if (b != null && b.isInMonth(currentMonth)) points += b.getNumberOfSeats() * 5;
        return points;
    }
}

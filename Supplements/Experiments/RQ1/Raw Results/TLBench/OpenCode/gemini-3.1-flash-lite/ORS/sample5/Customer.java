import java.util.List;
import java.util.ArrayList;

public class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings;

    public Customer() {
        this.bookings = new ArrayList<>();
    }

    public MembershipPackage getMembershipPackage() { return membershipPackage; }
    public void setMembershipPackage(MembershipPackage membershipPackage) { this.membershipPackage = membershipPackage; }
    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking booking) { this.bookings.add(booking); }

    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking b = new Booking();
        b.setCustomer(this);
        b.setTrip(trip);
        b.setNumberOfSeats(numberOfSeats);
        if (b.isBookingEligible()) {
            b.updateTripSeats();
            this.addBooking(b);
            trip.addBooking(b);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) return 0;
        int points = 0;
        for (Booking b : bookings) {
            if (b.isInMonth(currentMonth)) {
                points += b.getNumberOfSeats() * 5;
            }
        }
        return points;
    }
}

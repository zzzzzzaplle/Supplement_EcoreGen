import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings;

    public Customer() {
        this.bookings = new ArrayList<Booking>();
    }

    public MembershipPackage getMembershipPackage() {
        return membershipPackage;
    }

    public void setMembershipPackage(MembershipPackage membershipPackage) {
        this.membershipPackage = membershipPackage;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        if (this.bookings == null) {
            this.bookings = new ArrayList<Booking>();
        }
        this.bookings.add(booking);
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking b = new Booking();
        b.setCustomer(this);
        b.setTrip(trip);
        b.setNumberOfSeats(numberOfSeats);
        b.setBookingDate(new java.util.Date());
        if (b.isBookingEligible()) {
            b.updateTripSeats();
            trip.addBooking(b);
            this.addBooking(b);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        int points = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null && b.isInMonth(currentMonth)) {
                    points += b.getNumberOfSeats() * 5;
                }
            }
        }
        return points;
    }
}

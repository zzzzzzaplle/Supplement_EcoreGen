import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings;

    public Customer() {
        this.bookings = new ArrayList<>();
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
        this.bookings.add(booking);
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new java.util.Date());

        if (booking.isBookingEligible()) {
            trip.addBooking(booking);
            this.bookings.add(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null) {
            return 0;
        }
        MembershipPackage pkg = this.getMembershipPackage();
        if (pkg == null) {
            return 0;
        }
        if (!pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking b : bookings) {
            if (b.isInMonth(currentMonth)) {
                totalPoints += b.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }
}

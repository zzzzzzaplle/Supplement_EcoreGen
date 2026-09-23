import java.util.List;
import java.util.ArrayList;
import java.util.Date;

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
        if (bookings == null) {
            bookings = new ArrayList<Booking>();
        }
        if (booking != null) {
            bookings.add(booking);
        }
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        if (trip == null) {
            return;
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new Date());

        if (booking.isBookingEligible()) {
            addBooking(booking);
            booking.updateTripSeats();
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null) {
            return 0;
        }
        MembershipPackage pkg = membershipPackage;
        if (pkg == null) {
            return 0;
        }
        if (!pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        if (bookings == null) {
            return 0;
        }

        int totalPoints = 0;
        for (Booking b : bookings) {
            if (b == null) {
                continue;
            }
            if (b.isInMonth(currentMonth)) {
                totalPoints += b.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }
}

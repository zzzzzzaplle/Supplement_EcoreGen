import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings;

    public Customer() { }

    public MembershipPackage getMembershipPackage() {
        return membershipPackage;
    }

    public void setMembershipPackage(MembershipPackage membershipPackage) {
        this.membershipPackage = membershipPackage;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        bookings.add(booking);
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new Date());

        if (booking.isBookingEligible()) {
            trip.addBooking(booking);
            booking.updateTripSeats();
            this.addBooking(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (membershipPackage == null) {
            return 0;
        }
        if (!membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        if (bookings == null) {
            return 0;
        }

        int total = 0;
        for (Booking b : bookings) {
            if (b != null && b.isInMonth(currentMonth)) {
                total += 5 * b.getNumberOfSeats();
            }
        }
        return total;
    }
}

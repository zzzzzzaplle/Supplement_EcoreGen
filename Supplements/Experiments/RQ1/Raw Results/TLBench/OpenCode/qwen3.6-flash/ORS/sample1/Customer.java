import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

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
        if (booking != null) {
            this.bookings.add(booking);
        }
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new Date());
        if (booking.isBookingEligible()) {
            booking.updateTripSeats();
            this.addBooking(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null) {
            return 0;
        }
        if (this.membershipPackage == null || !this.membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        Set<Trip> tripsInMonth = new HashSet<>();
        for (Booking b : this.bookings) {
            if (b.isInMonth(currentMonth)) {
                tripsInMonth.add(b.getTrip());
            }
        }
        int total = 0;
        for (Trip t : tripsInMonth) {
            total += t.calculateMonthlyPoints(this, currentMonth);
        }
        return total;
    }
}

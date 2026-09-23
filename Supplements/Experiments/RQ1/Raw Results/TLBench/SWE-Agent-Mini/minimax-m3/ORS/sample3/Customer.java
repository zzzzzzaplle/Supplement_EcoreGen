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

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        if (this.bookings == null) {
            this.bookings = new ArrayList<Booking>();
        }
        this.bookings.add(booking);
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        if (trip == null) {
            return;
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new java.util.Date());
        if (booking.isBookingEligible()) {
            booking.updateTripSeats();
            this.addBooking(booking);
            trip.addBooking(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (this.membershipPackage == null || !this.membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        if (this.bookings == null || currentMonth == null) {
            return 0;
        }
        int total = 0;
        for (Booking b : this.bookings) {
            if (b != null && b.isInMonth(currentMonth)) {
                total += b.getNumberOfSeats() * 5;
            }
        }
        return total;
    }
}

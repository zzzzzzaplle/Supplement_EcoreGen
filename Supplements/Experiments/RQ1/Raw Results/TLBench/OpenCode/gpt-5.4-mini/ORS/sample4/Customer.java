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
        if (booking != null) {
            if (bookings == null) {
                bookings = new ArrayList<Booking>();
            }
            bookings.add(booking);
        }
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new java.util.Date());
        if (booking.isBookingEligible()) {
            addBooking(booking);
            if (trip != null) {
                trip.addBooking(booking);
            }
            booking.updateTripSeats();
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (bookings == null) {
            return 0;
        }
        int total = 0;
        for (Booking booking : bookings) {
            if (booking != null) {
                total += booking.getTrip() == null ? 0 : booking.getTrip().calculateMonthlyPoints(this, currentMonth);
            }
        }
        return total;
    }
}

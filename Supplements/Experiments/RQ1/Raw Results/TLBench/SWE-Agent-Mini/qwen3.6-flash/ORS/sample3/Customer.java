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
        if (booking != null && bookings != null) {
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
            booking.updateTripSeats();
            trip.addBooking(booking);
            addBooking(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null) {
            return 0;
        }
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null && b.isInMonth(currentMonth)) {
                    totalPoints += b.getNumberOfSeats() * 5;
                }
            }
        }
        return totalPoints;
    }
}

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
        this.bookings.add(booking);
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new java.util.Date());
        
        if (booking.isBookingEligible()) {
            // Add booking to customer's list
            addBooking(booking);
            // Add booking to trip's list
            trip.addBooking(booking);
            // Update trip seats (the number of available seats is reduced)
            // The numberOfSeats field represents total capacity, we deduct
            int availableSeats = trip.getNumberOfSeats() - trip.getBookedSeats();
            // seats are tracked via bookings, so total seats - booked seats = available
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null) {
            return 0;
        }
        MembershipPackage pkg = getMembershipPackage();
        if (pkg == null || !pkg.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        if (bookings != null) {
            for (Booking booking : bookings) {
                if (booking != null && booking.isInMonth(currentMonth)) {
                    totalPoints += booking.getNumberOfSeats() * 5;
                }
            }
        }
        return totalPoints;
    }
}

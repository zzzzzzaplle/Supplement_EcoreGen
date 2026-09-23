import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings;

    public Customer() {
        bookings = new ArrayList<Booking>();
    }

    public MembershipPackage getMembershipPackage() { return membershipPackage; }
    public void setMembershipPackage(MembershipPackage membershipPackage) { this.membershipPackage = membershipPackage; }
    public List<Booking> getBookings() { return bookings; }
    public void addBooking(Booking booking) { if (booking != null) bookings.add(booking); }

    public void bookTrip(Trip trip, int numberOfSeats) {
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new Date());
        if (booking.isBookingEligible()) {
            addBooking(booking);
            if (trip != null) trip.addBooking(booking);
            booking.updateTripSeats();
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (membershipPackage == null || !membershipPackage.hasAward(Award.POINTS) || bookings == null || currentMonth == null) {
            return 0;
        }
        int total = 0;
        for (Booking b : bookings) {
            if (b != null && b.isInMonth(currentMonth)) {
                total += b.getNumberOfSeats() * 5;
            }
        }
        return total;
    }
}

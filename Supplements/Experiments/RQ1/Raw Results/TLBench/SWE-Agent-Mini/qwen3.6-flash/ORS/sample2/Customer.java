import java.util.*;

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
        if (booking != null && !bookings.contains(booking)) {
            bookings.add(booking);
        }
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        if (trip == null || numberOfSeats <= 0) {
            return;
        }
        
        Booking booking = new Booking();
        booking.setNumberOfSeats(numberOfSeats);
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setBookingDate(new Date());
        
        if (booking.isBookingEligible()) {
            bookings.add(booking);
            trip.addBooking(booking);
            booking.updateTripSeats();
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        return currentMonth == null ? 0 : 0;
    }
}

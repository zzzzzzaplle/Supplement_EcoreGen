import java.util.List;
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

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        if (booking != null && this.bookings != null) {
            this.bookings.add(booking);
        }
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        if (trip == null) {
            return;
        }
        Booking booking = new Booking();
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setCustomer(this);
        if (booking.isBookingEligible()) {
            booking.updateTripSeats();
            addBooking(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null || currentMonth.isEmpty()) {
            return 0;
        }
        if (this.membershipPackage == null) {
            return 0;
        }
        if (!this.membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        int[] totalPoints = new int[]{0};
        int[] pointsPerSeat = new int[]{5};
        for (Booking booking : this.bookings) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                int seats = booking.getNumberOfSeats();
                if (seats <= 0) {
                    seats = 1;
                }
                totalPoints[0] += seats * pointsPerSeat[0];
            }
        }
        return totalPoints[0];
    }
}

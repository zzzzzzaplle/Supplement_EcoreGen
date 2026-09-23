import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

class Customer extends User {
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
            bookings.add(booking);
            // Also add to trip
            if (booking.getTrip() != null) {
                booking.getTrip().addBooking(booking);
            }
        }
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        if (trip == null || numberOfSeats <= 0) {
            return;
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new Date());

        if (booking.isBookingEligible()) {
            addBooking(booking);
            // Update trip seat count by reducing available seats
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        return calculateMonthlyRewardPoints(currentMonth);
    }

    private int calculateMonthlyRewardPoints(String currentMonth) {
        if (membershipPackage == null || currentMonth == null || currentMonth.isEmpty()) {
            return 0;
        }

        if (!membershipPackage.hasAward(Award.POINTS)) {
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

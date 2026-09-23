import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

class Customer extends User {
    private MembershipPackage membershipPackage;
    private List<Booking> bookings;

    public Customer() {
        bookings = new ArrayList<>();
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
        bookings.add(booking);
    }

    public void bookTrip(Trip trip, int numberOfSeats, LocalDateTime bookingTime) {
        if (trip == null || bookingTime == null) {
            return;
        }
        Booking booking = new Booking();
        booking.setNumberOfSeats(numberOfSeats);
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setBookingDate(LocalDate.now());
        if (booking.isBookingEligible(bookingTime)) {
            bookings.add(booking);
            trip.addBooking(booking);
            trip.setNumberOfSeats(trip.getNumberOfSeats() - numberOfSeats);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (currentMonth == null || membershipPackage == null) {
            return 0;
        }
        if (!membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        int totalPoints = 0;
        for (Booking booking : bookings) {
            if (booking != null && booking.isInMonth(currentMonth)) {
                totalPoints += booking.getNumberOfSeats() * 5;
            }
        }
        return totalPoints;
    }
}

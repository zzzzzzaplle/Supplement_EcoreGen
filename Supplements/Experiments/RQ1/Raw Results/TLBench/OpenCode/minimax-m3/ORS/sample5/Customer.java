import java.util.ArrayList;
import java.util.Date;
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
        if (bookings == null) {
            bookings = new ArrayList<Booking>();
        }
        bookings.add(booking);
    }

    public void bookTrip(Trip trip, int numberOfSeats) {
        if (trip == null) {
            return;
        }
        Booking booking = new Booking();
        booking.setCustomer(this);
        booking.setTrip(trip);
        booking.setNumberOfSeats(numberOfSeats);
        booking.setBookingDate(new Date());

        if (booking.isBookingEligible()) {
            booking.updateTripSeats();
            addBooking(booking);
        }
    }

    public int computeMonthlyRewardPoints(String currentMonth) {
        if (membershipPackage == null) {
            return 0;
        }
        if (!membershipPackage.hasAward(Award.POINTS)) {
            return 0;
        }
        if (currentMonth == null) {
            return 0;
        }

        int points = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b.isInMonth(currentMonth)) {
                    points += b.getNumberOfSeats() * 5;
                }
            }
        }
        return points;
    }
}
